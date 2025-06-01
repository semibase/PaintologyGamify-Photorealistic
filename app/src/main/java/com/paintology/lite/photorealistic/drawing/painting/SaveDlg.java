package com.paintology.lite.photorealistic.drawing.painting;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.paintology.lite.photorealistic.drawing.R;


public class SaveDlg extends Dialog {

    public interface OnProjectNameListener {
        void onOk(SaveDlg dialog, Painting pPainting, boolean bSave, String strProjectName);

        void onCancel();
    }

    private Button m_btnQuestionOK;
    private Button m_btnCancel;
    private ImageView imgCross;
    private EditText m_txtProjectName;

    private OnProjectNameListener m_listener;

    private Painting m_painting;
    private Context m_context;

    public boolean m_bSave;
    public static boolean g_bNewCanvasFlag = true;

    public SaveDlg(Context context, Painting pPainting, String strProjectName, boolean bSave, OnProjectNameListener listener) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.savedlg);

        m_context = context;
        m_listener = listener;
        m_painting = pPainting;
        m_bSave = bSave;


        m_txtProjectName = (EditText) findViewById(R.id.txtprojectname);
        m_txtProjectName.setText(strProjectName);

        m_txtProjectName.setSelection(m_txtProjectName.getText().length());

        // Set focus change listener to select all text when EditText gains focus
        m_txtProjectName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                m_txtProjectName.selectAll();
                m_txtProjectName.setSelectAllOnFocus(true);
           }

        });

        initButton();
    }

    private void initButton() {

        m_btnQuestionOK = (Button) findViewById(R.id.btn_projectnamesave);
        imgCross = (ImageView) findViewById(R.id.imgCross);
        m_btnCancel = (Button) findViewById(R.id.btn_cancel);

        m_btnQuestionOK.setOnClickListener(v -> {
            if (!m_txtProjectName.getText().toString().isEmpty()) {
                SaveDlg.this.m_listener.onOk(SaveDlg.this, m_painting, m_bSave, m_txtProjectName.getText().toString());
            } else {
                Toast.makeText(m_context, "Enter File Name!", Toast.LENGTH_SHORT).show();
            }
        });

        m_btnCancel.setOnClickListener(v -> {
            SaveDlg.this.m_listener.onCancel();
            SaveDlg.this.cancel();
        });

        imgCross.setOnClickListener(v -> {
            SaveDlg.this.m_listener.onCancel();
            SaveDlg.this.cancel();
        });


        m_txtProjectName.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                if (!m_txtProjectName.getText().toString().isEmpty()) {
                    SaveDlg.this.m_listener.onOk(SaveDlg.this, m_painting, m_bSave, m_txtProjectName.getText().toString());
                    SaveDlg.this.cancel();
                } else {
                    m_txtProjectName.setError("Required!");
                }
            }
            return false;
        });
    }

    protected void onStop() {
        g_bNewCanvasFlag = true;
        super.onStop();
    }
}
