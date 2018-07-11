package com.example.usrdel.servidores;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.microsoft.bing.speech.SpeechClientStatus;
import com.microsoft.cognitiveservices.speechrecognition.DataRecognitionClient;
import com.microsoft.cognitiveservices.speechrecognition.ISpeechRecognitionServerEvents;
import com.microsoft.cognitiveservices.speechrecognition.MicrophoneRecognitionClient;
import com.microsoft.cognitiveservices.speechrecognition.RecognitionResult;
import com.microsoft.cognitiveservices.speechrecognition.RecognitionStatus;
import com.microsoft.cognitiveservices.speechrecognition.SpeechRecognitionMode;
import com.microsoft.cognitiveservices.speechrecognition.SpeechRecognitionServiceFactory;

public class STT extends AppCompatActivity implements ISpeechRecognitionServerEvents {

    int m_waitSeconds = 0;
    DataRecognitionClient dataClient = null;
    MicrophoneRecognitionClient micClient = null;
    FinalResponseStatus isReceivedResponse = FinalResponseStatus.NotReceived;
    EditText _logText;
    RadioGroup _radioGroup;
    Button _buttonSelectMode;
    Button _startButton;

    public enum FinalResponseStatus { NotReceived, OK, Timeout }
    public String getPrimaryKey() {
        return this.getString(R.string.primaryKey);
    }
    private String getAuthenticationUri() {
        return this.getString(R.string.authenticationUri);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stt);

        this._logText = (EditText) findViewById(R.id.editText1);
        this._startButton = (Button) findViewById(R.id.button1);

        Toast toast = Toast.makeText(this, "Start STT", Toast.LENGTH_LONG);
        toast.show();

        if (getString(R.string.primaryKey).startsWith("Please")) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.add_subscription_key_tip_title))
                    .setMessage(getString(R.string.add_subscription_key_tip))
                    .setCancelable(false)
                    .show();
        }

        // setup the buttons
        final STT This = this;
        this._startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                This.StartButton_Click(arg0);
            }
        });

    }

    private SpeechRecognitionMode getMode() {
        return SpeechRecognitionMode.LongDictation;
    }

    private void StartButton_Click(View arg0) {
        this._startButton.setEnabled(false);

        this.m_waitSeconds = this.getMode() == SpeechRecognitionMode.ShortPhrase ? 20 : 200;


        this.micClient = SpeechRecognitionServiceFactory.createMicrophoneClient(
                this,
                getMode(),
                "en-us",
                this,
                this.getPrimaryKey());

        this.micClient.setAuthenticationUri(this.getAuthenticationUri());
        this.micClient.startMicAndRecognition();

    }

    public void onAudioEvent(boolean recording) {
        this.WriteLine("--- Microphone status change received by onAudioEvent() ---");
        this.WriteLine("********* Microphone status: " + recording + " *********");
        if (recording) {
            //this.WriteLine("Please start speaking.");
            Toast toast = Toast.makeText(this, "Start Speaking", Toast.LENGTH_LONG);
            toast.show();
        }

        WriteLine();
        if (!recording) {
            this.micClient.endMicAndRecognition();
            this._startButton.setEnabled(true);
        }
    }

    private void WriteLine() {
        this.WriteLine("");
    }

    private void WriteLine(String text) {
        this._logText.append(text + "\n");
    }

    public void onPartialResponseReceived(final String response) {
        this.WriteLine("--- Partial result received by onPartialResponseReceived() ---");
        this.WriteLine(response);
        this.WriteLine();
    }

    public void onFinalResponseReceived(final RecognitionResult response) {
        boolean isFinalDicationMessage = this.getMode() == SpeechRecognitionMode.LongDictation &&
                (response.RecognitionStatus == RecognitionStatus.EndOfDictation ||
                        response.RecognitionStatus == RecognitionStatus.DictationEndSilenceTimeout);
        if (null != this.micClient && true && ((this.getMode() == SpeechRecognitionMode.ShortPhrase) || isFinalDicationMessage)) {
            // we got the final result, so it we can end the mic reco.  No need to do this
            // for dataReco, since we already called endAudio() on it as soon as we were done
            // sending all the data.
            this.micClient.endMicAndRecognition();
        }

        if (isFinalDicationMessage) {
            this._startButton.setEnabled(true);
            this.isReceivedResponse = FinalResponseStatus.OK;
        }

        if (!isFinalDicationMessage) {
            this.WriteLine("********* Final n-BEST Results *********");
            for (int i = 0; i < response.Results.length; i++) {
                this.WriteLine("[" + i + "]" + " Confidence=" + response.Results[i].Confidence +
                        " Text=\"" + response.Results[i].DisplayText + "\"");
            }

            this.WriteLine();
            this.micClient.endMicAndRecognition();
        }
    }

    public void onIntentReceived(final String payload) {
        this.WriteLine("--- Intent received by onIntentReceived() ---");
        this.WriteLine(payload);
        this.WriteLine();
    }

    public void onError(final int errorCode, final String response) {
        this._startButton.setEnabled(true);
        this.WriteLine("--- Error received by onError() ---");
        this.WriteLine("Error code: " + SpeechClientStatus.fromInt(errorCode) + " " + errorCode);
        this.WriteLine("Error text: " + response);
        this.WriteLine();
    }


/*    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }*/
}
