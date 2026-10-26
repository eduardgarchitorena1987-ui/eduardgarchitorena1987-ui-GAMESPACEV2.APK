package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * High-performance sci-fi sound effects synthesizer for CEDSII SPACE.
 * Generates dynamic audio in-memory using AudioTrack and 16-bit PCM waveforms,
 * requiring zero external audio assets, zero network latency, and guaranteed playback.
 */
object GamingAudioFx {
    private val scope = CoroutineScope(Dispatchers.Default)
    private const val SAMPLE_RATE = 44100

    /**
     * Cool futuristic Game Space entrance sound:
     * Exponential cyber warp sweep (140Hz -> 1600Hz) with resonant harmonics,
     * sub-bass rumble, and dual-tone chime finish.
     */
    fun playGameSpaceIntroSound() {
        scope.launch {
            try {
                val durationMs = 850
                val numSamples = (SAMPLE_RATE * durationMs / 1000)
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    val progress = i.toDouble() / numSamples

                    // Exponential upward frequency sweep: 160Hz to 1800Hz
                    val currentFreq = 160.0 * Math.pow(1800.0 / 160.0, progress)
                    val phase = 2.0 * PI * currentFreq * t

                    // Harmonic overtones
                    val fundamental = sin(phase)
                    val harmonic2 = 0.35 * sin(phase * 2.0)
                    val harmonic3 = 0.20 * sin(phase * 3.0)

                    // Sub-bass resonance during the first 300ms
                    val subBass = if (t < 0.35) {
                        0.6 * sin(2.0 * PI * 65.0 * t) * (1.0 - t / 0.35)
                    } else 0.0

                    // Chime flourish in the last 250ms
                    val chime = if (t > 0.6) {
                        val chimeT = t - 0.6
                        0.4 * (sin(2.0 * PI * 1760.0 * chimeT) + sin(2.0 * PI * 2640.0 * chimeT)) * exp(-chimeT * 12.0)
                    } else 0.0

                    // Overall envelope: smooth attack, sustained body, decay at the end
                    val envelope = when {
                        progress < 0.1 -> progress / 0.1
                        progress > 0.8 -> (1.0 - progress) / 0.2
                        else -> 1.0
                    }

                    val sampleValue = (fundamental + harmonic2 + harmonic3 + subBass + chime) * envelope * 0.75
                    val clamped = (sampleValue.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt()
                    buffer[i] = clamped.toShort()
                }

                playPcmBuffer(buffer)
            } catch (_: Exception) {}
        }
    }

    /**
     * Tactile mechanical-glass click when touching or expanding the bubble.
     */
    fun playOrbClickSound() {
        scope.launch {
            try {
                val durationMs = 35
                val numSamples = (SAMPLE_RATE * durationMs / 1000)
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    val progress = i.toDouble() / numSamples
                    val freq = 1400.0 - (progress * 500.0)
                    val wave = sin(2.0 * PI * freq * t)
                    val envelope = exp(-progress * 9.0)
                    val sample = (wave * envelope * 0.7 * Short.MAX_VALUE).toInt()
                    buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playPcmBuffer(buffer)
            } catch (_: Exception) {}
        }
    }

    /**
     * Supersonic thruster warp sound when running RAM purge or Turbo Boost.
     */
    fun playTurboBoostSound() {
        scope.launch {
            try {
                val durationMs = 450
                val numSamples = (SAMPLE_RATE * durationMs / 1000)
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    val progress = i.toDouble() / numSamples
                    val freq = 220.0 + 800.0 * sin(progress * PI)
                    val wave = sin(2.0 * PI * freq * t)
                    val sub = 0.5 * sin(2.0 * PI * 80.0 * t)
                    val envelope = (1.0 - progress) * (if (progress < 0.15) progress / 0.15 else 1.0)
                    val sample = ((wave + sub) * envelope * 0.8 * Short.MAX_VALUE).toInt()
                    buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playPcmBuffer(buffer)
            } catch (_: Exception) {}
        }
    }

    /**
     * High-tech cyber chime on toggle switch.
     */
    fun playToggleSound(enabled: Boolean) {
        scope.launch {
            try {
                val durationMs = 60
                val numSamples = (SAMPLE_RATE * durationMs / 1000)
                val buffer = ShortArray(numSamples)
                val baseFreq = if (enabled) 880.0 else 520.0
                val targetFreq = if (enabled) 1320.0 else 380.0

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    val progress = i.toDouble() / numSamples
                    val freq = baseFreq + (targetFreq - baseFreq) * progress
                    val wave = sin(2.0 * PI * freq * t)
                    val envelope = 1.0 - progress
                    val sample = (wave * envelope * 0.6 * Short.MAX_VALUE).toInt()
                    buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playPcmBuffer(buffer)
            } catch (_: Exception) {}
        }
    }

    private fun playPcmBuffer(buffer: ShortArray) {
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(buffer.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(buffer, 0, buffer.size)
        audioTrack.play()

        // Release track after playback completes
        val durationMs = (buffer.size * 1000L) / SAMPLE_RATE + 100L
        scope.launch {
            kotlinx.coroutines.delay(durationMs)
            try {
                audioTrack.stop()
                audioTrack.release()
            } catch (_: Exception) {}
        }
    }
}
