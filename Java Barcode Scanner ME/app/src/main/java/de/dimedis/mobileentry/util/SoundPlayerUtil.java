package de.dimedis.mobileentry.util;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import de.dimedis.mobileentry.R;
import de.dimedis.mobileentry.bbapi.Constants;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SoundPlayerUtil implements Observer<Integer> {
    static final String TAG = "SoundPlayerUtil";
    public static SoundPlayerUtil instance = null;
    private Context context;

    public static SoundPlayerUtil getInstance() {
        if (instance == null) {
            instance = new SoundPlayerUtil(AppContext.get());
        }
        return instance;
    }

    private SoundPlayerUtil(Context context) {
        setContext(context);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void playSoundAsync(SoundType type) {
        switch (type) {

            case TICKET_SCAN_STATUS_OK:
                playSoundFileAsync(R.raw.code_scan_ok);
                if (PrefUtils.isVibrationEnabled()) vibratePattern(Constants.VIBRATION_PATTERN_OK);
                break;
            case TICKET_SCAN_STATUS_OK_REDUCED_TICKET:
                playSoundFileAsync(R.raw.ticket_entry_ok_reduced);
                if (PrefUtils.isVibrationEnabled())
                    vibratePattern(Constants.VIBRATION_PATTERN_OK_REDUCED_TICKET);
                break;
            case TICKET_SCAN_STATUS_NOT_OK:
                playSoundFileAsync(R.raw.ticket_not_ok);
                if (PrefUtils.isVibrationEnabled())
                    vibratePattern(Constants.VIBRATION_PATTERN_TICKET_STATUS_NOT_OK);
                break;
            case CODE_SCAN_OK_OR_MANUAL_ACCEPTED:
                playSoundFileAsync(R.raw.ticket_entry_ok);
                if (PrefUtils.isVibrationEnabled())
                    vibratePattern(Constants.VIBRATION_PATTERN_TICKET_ACCEPTED);
                break;
            case CODE_SCAN_REFUSED_OR_ALREADY_PROCESSING:
                playSoundFileAsync(R.raw.code_scan_refused);
                if (PrefUtils.isVibrationEnabled())
                    vibratePattern(Constants.VIBRATION_PATTERN_CODE_REFUSED);
                break;
            case GENERAL_ERROR:
                playSoundFileAsync(R.raw.general_error);
                if (PrefUtils.isVibrationEnabled())
                    vibratePattern(Constants.VIBRATION_PATTERN_GENERAL_ERROR);
                break;

            default:
                throw new IllegalArgumentException(type.name() + " is not supported!");
        }
    }

    private void vibratePattern(final long[] vibrationPattern) {
        if (vibrationPattern == null || vibrationPattern.length == 0) {
            Log.d(this.getClass().getSimpleName(), "Vibration pattern is empty");
            return;
        }
        Observable<Integer> observable = Observable.create(emitter -> {
            try {
                Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                VibrationEffect vibrationEffect = VibrationEffect.createWaveform(vibrationPattern, -1);
                vibrator.vibrate(vibrationEffect);
            } catch (Throwable throwable) {
                emitter.onError(throwable);
            }
            emitter.onComplete();
        });
        observable.subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(this);
    }

    private void playSoundFileAsync(final int rawFileId) {
        Observable<Integer> observable = Observable.create(emitter -> {
            MediaPlayer mediaPlayer = null;
            try {
                mediaPlayer = MediaPlayer.create(getContext().getApplicationContext(), rawFileId);
                mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                    Log.e(TAG, "Media problem");
                    return false;
                });
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();
                mediaPlayer.setAudioAttributes(audioAttributes);
                mediaPlayer.setOnCompletionListener(mp -> {
                    emitter.onComplete();
                });
                mediaPlayer.start();
            } catch (Throwable thr) {
                thr.printStackTrace();
                emitter.onError(thr);
            }
        });
        observable.subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(this);
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
    }

    @Override
    public void onNext(Integer integer) {
    }
}
