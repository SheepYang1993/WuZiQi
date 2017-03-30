package me.sheepyang.wuziqi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.sheepyang.wuziqi.widget.Panel;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.panel)
    Panel mPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }


    @Override
    @OnClick({R.id.btn_restart, R.id.btn_give_up, R.id.btn_undo})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_restart:
                mPanel.restartGame();
                break;
            case R.id.btn_give_up:
                mPanel.giveUp();
                break;
            case R.id.btn_undo:
                mPanel.undo();
                break;
            default:
                break;
        }
    }
}
