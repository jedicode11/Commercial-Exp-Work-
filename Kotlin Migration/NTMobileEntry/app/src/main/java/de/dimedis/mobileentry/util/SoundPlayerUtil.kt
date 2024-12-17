package de.dimedis.mobileentry.util

import android.content.Context
import android.util.Log
import de.dimedis.mobileentry.bbapi.Constants
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.schedulers.Schedulers
import java.lang.IllegalArgumentException

import android.os.Vibrator
import android.media.MediaPlayer
import android.media.AudioManager
import de.dimedis.mobileentry.R
import io.reactivex.rxjava3.disposables.Disposable

class SoundPlayerUtil private constructor(context: Context) : Observer<Int?> {

    private var context: Context? = null

    init {
        setContext(context)
    }

    fun setContext(context: Context?) {
        this.context = context
    }

    fun getContext(): Context? {
        return context
    }

    fun playSoundAsync(type: SoundType) {
        when (type) {
            SoundType.TICKET_SCAN_STATUS_OK -> {
                playSoundFileAsync(R.raw.code_scan_ok)
                if (PrefUtils.isVibrationEnabled()) vibratePattern(Constants.VIBRATION_PATTERN_OK)
            }
            SoundType.TICKET_SCAN_STATUS_OK_REDUCED_TICKET -> {
                playSoundFileAsync(R.raw.ticket_entry_ok_reduced)
                if (PrefUtils.isVibrationEnabled()) vibratePattern(Constants.VIBRATION_PATTERN_OK_REDUCED_TICKET)
            }
            SoundType.TICKET_SCAN_STATUS_NOT_OK -> {
                playSoundFileAsync(R.raw.ticket_not_ok)
                if (PrefUtils.isVibrationEnabled()) vibratePattern(Constants.VIBRATION_PATTERN_TICKET_STATUS_NOT_OK)
            }
            SoundType.CODE_SCAN_OK_OR_MANUAL_ACCEPTED -> {
                playSoundFileAsync(R.raw.ticket_entry_ok)
                if (PrefUtils.isVibrationEnabled()) vibratePattern(Constants.VIBRATION_PATTERN_TICKET_ACCEPTED)
            }
            SoundType.CODE_SCAN_REFUSED_OR_ALREADY_PROCESSING -> {
                playSoundFileAsync(R.raw.code_scan_refused)
                if (PrefUtils.isVibrationEnabled()) vibratePattern(Constants.VIBRATION_PATTERN_CODE_REFUSED)
            }
            SoundType.GENERAL_ERROR -> {
                playSoundFileAsync(R.raw.general_error)
                if (PrefUtils.isVibrationEnabled()) vibratePattern(Constants.VIBRATION_PATTERN_GENERAL_ERROR)
            }
        }
    }

    private fun vibratePattern(vibrationPattern: LongArray?) {
        if (vibrationPattern == null || vibrationPattern.isEmpty()) {
            Log.d(this.javaClass.simpleName, "vibration pattern is empty")
            return
        }
        val observable = Observable.create<Int> { emitter ->
            try {
                val vibrator = getContext()!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                // Only perform this pattern one time (-1 means "do not repeat")
                vibrator.vibrate(vibrationPattern, -1)
            } catch (throwable: Throwable) {
                emitter.onError(throwable)
            }
            emitter.onComplete()
        }
        observable.subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(this)
    }

    private fun playSoundFileAsync(rawFileId: Int) {
        val observable = Observable.create<Int> { emitter ->
            var mediaPlayer: MediaPlayer? = null
            try {
                mediaPlayer = MediaPlayer.create(getContext()!!.applicationContext, rawFileId)
                mediaPlayer.setOnErrorListener(MediaPlayer.OnErrorListener { mp, what, extra ->
                    Logger.e(TAG, "media problem")
                    false
                })
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
                mediaPlayer.start()
                while (mediaPlayer.isPlaying) {
                    Thread.sleep(100)
                }
            } catch (thr: Throwable) {
                thr.printStackTrace()
                emitter.onError(thr)
            } finally {
                if (mediaPlayer != null) {
                    mediaPlayer.stop()
                    mediaPlayer.release()
                }
            }
            emitter.onComplete()
        }
        observable.subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(this)
    }

    override fun onComplete() {}
    override fun onError(e: Throwable) {
        e.printStackTrace()
    }

    override fun onSubscribe(d: Disposable) {}
    override fun onNext(integer: Int?) {}

    companion object {
        const val TAG = "SoundPlayerUtil"
        var instance: SoundPlayerUtil? = null
        @JvmName("getInstance1")
        fun getInstance(): SoundPlayerUtil {
            if (instance == null) {
                instance = SoundPlayerUtil(AppContext.get())
            }
            return instance as SoundPlayerUtil
        }
    }
}