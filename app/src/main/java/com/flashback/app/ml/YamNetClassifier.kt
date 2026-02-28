package com.flashback.app.ml

import android.content.Context
import com.flashback.app.model.AppConstants
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

/**
 * YAMNet TFLite 分類器。
 * 輸入：15,600 個 Float[-1,1] 正規化 PCM 樣本（約 0.975 秒 @ 16kHz）
 * 輸出：521 個類別的信心度分數
 */
class YamNetClassifier(context: Context) : SoundClassifier {

    private val interpreter: Interpreter
    private val labels = YamNetLabels.LABELS

    init {
        val model = loadModelFile(context, "yamnet.tflite")
        val options = Interpreter.Options().apply {
            setNumThreads(2)
        }
        interpreter = Interpreter(model, options)
        // YAMNet 模型的 input tensor 預設 shape 為 [1]，需 resize 為 [15600]
        interpreter.resizeInput(0, intArrayOf(AppConstants.YAMNET_INPUT_SAMPLES))
        interpreter.allocateTensors()
    }

    override fun classify(samples: ShortArray): ClassificationResult {
        // 將 16-bit PCM 轉換為 Float[-1,1]
        val inputSize = AppConstants.YAMNET_INPUT_SAMPLES
        val inputBuffer = ByteBuffer.allocateDirect(inputSize * 4)
            .order(ByteOrder.nativeOrder())

        for (i in 0 until inputSize) {
            val sample = if (i < samples.size) samples[i] else 0
            inputBuffer.putFloat(sample.toFloat() / 32768f)
        }
        inputBuffer.rewind()

        // YAMNet 輸出：多個 output tensors
        // output[0]: scores [num_frames, 521]
        val outputShape = interpreter.getOutputTensor(0).shape()
        val numFrames = outputShape[0]
        val numClasses = outputShape[1]
        val outputScores = Array(numFrames) { FloatArray(numClasses) }

        val outputMap = HashMap<Int, Any>()
        outputMap[0] = outputScores

        // 如果有多個輸出 tensor，分配空間
        for (i in 1 until interpreter.outputTensorCount) {
            val shape = interpreter.getOutputTensor(i).shape()
            outputMap[i] = Array(shape[0]) { FloatArray(shape.last()) }
        }

        interpreter.runForMultipleInputsOutputs(arrayOf(inputBuffer), outputMap)

        // 對所有 frames 的分數取平均
        val avgScores = FloatArray(numClasses)
        for (frame in 0 until numFrames) {
            for (cls in 0 until numClasses) {
                avgScores[cls] += outputScores[frame][cls]
            }
        }
        for (cls in 0 until numClasses) {
            avgScores[cls] /= numFrames
        }

        // 找出 top-5 結果
        val topIndices = avgScores.indices
            .sortedByDescending { avgScores[it] }
            .take(5)

        val topResults = topIndices.map { idx ->
            val label = if (idx < labels.size) labels[idx] else "Unknown"
            label to avgScores[idx]
        }

        val bestIdx = topIndices.first()
        val bestLabel = if (bestIdx < labels.size) labels[bestIdx] else "Unknown"

        return ClassificationResult(
            label = bestLabel,
            confidence = avgScores[bestIdx],
            topResults = topResults
        )
    }

    override fun close() {
        interpreter.close()
    }

    private fun loadModelFile(context: Context, filename: String): MappedByteBuffer {
        val assetFileDescriptor = context.assets.openFd(filename)
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        return fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            assetFileDescriptor.startOffset,
            assetFileDescriptor.declaredLength
        )
    }
}
