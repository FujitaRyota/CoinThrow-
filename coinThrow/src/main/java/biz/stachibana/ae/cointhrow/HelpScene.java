package biz.stachibana.ae.cointhrow;

import android.view.KeyEvent;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by sys314 on 2015/07/16.
 */
public class HelpScene extends KeyListenScene implements ButtonSprite.OnClickListener
{
    private static final int HELP_BACK = 1;
    private static final int INITIAL_RANKING = 2;
    private static final int INITIAL_RECOMMEND = 3;

    // ボタンが押された時の効果音
    private Sound btnPressedSound;

    public HelpScene(MultiSceneActivity context) {
        super(context);
        init();
    }

    @Override
    public void init() {

        Sprite bg = getBaseActivity().getResourceUtil()
                .getSprite("help_bg.png");
        bg.setPosition(0, 0);
        attachChild(bg);

        Sprite titleSprite = getBaseActivity().getResourceUtil().getSprite(
                "help_title.png");
        placeToCenterX(titleSprite, 200);
        attachChild(titleSprite);

        // ボタンの追加
        ButtonSprite btnStart = getBaseActivity().getResourceUtil()
                .getButtonSprite("help_btn_back.png",
                        "help_btn_back_p.png");
        placeToCenterX(btnStart, 680);
        btnStart.setTag(HELP_BACK);
        btnStart.setOnClickListener(this);
        attachChild(btnStart);
        // ボタンをタップ可能に
        registerTouchArea(btnStart);
/*
        ButtonSprite btnRanking = getBaseActivity().getResourceUtil()
                .getButtonSprite("initial_btn_ranking.png",
                        "initial_btn_ranking_p.png");
        placeToCenterX(btnRanking, 560);
        btnRanking.setTag(INITIAL_RANKING);
        btnRanking.setOnClickListener(this);
        attachChild(btnRanking);
        registerTouchArea(btnRanking);

        ButtonSprite btnRecommend = getBaseActivity().getResourceUtil()
                .getButtonSprite("initial_btn_recommend.png",
                        "initial_btn_recommend_p.png");
        placeToCenterX(btnRecommend, 640);
        btnRecommend.setTag(INITIAL_RECOMMEND);
        btnRecommend.setOnClickListener(this);
        attachChild(btnRecommend);
        registerTouchArea(btnRecommend);
*/
    }

    @Override
    public void prepareSoundAndMusic() {
        // 効果音をロード
        try {
            btnPressedSound = SoundFactory.createSoundFromAsset(
                    getBaseActivity().getSoundManager(), getBaseActivity(),
                    "clock00.wav");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        return false;
    }

    public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
                        float pTouchAreaLocalY) {
        // 効果音を再生
        btnPressedSound.play();

        KeyListenScene scene = new MainScene(getBaseActivity());
        switch (pButtonSprite.getTag()) {
            case HELP_BACK:
                //getBaseActivity().backToInitial();
                // ひとつ前のシーンを取得
                MainActivity mainActivity = (MainActivity)getBaseActivity();
                mainActivity.backToPrevious();
                break;
/*
            case INITIAL_RANKING:
                break;
            case INITIAL_RECOMMEND:
                System.out.println("おすすめアプリ！");
                MainScene.showHelp();
                break;
*/
        }
    }
}
