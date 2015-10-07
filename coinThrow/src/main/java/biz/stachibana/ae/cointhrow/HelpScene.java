package biz.stachibana.ae.cointhrow;

import android.view.KeyEvent;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by sys314 on 2015/07/16.
 */
public class HelpScene extends KeyListenScene implements ButtonSprite.OnClickListener
{
    private static final int HELP_BACK = 1;
    private static final int INITIAL_RANKING = 2;
    private static final int INITIAL_RECOMMEND = 3;

    private HashMap<String,Sprite> spriteMap;
    private ButtonSprite btnStart;
    private Sprite titleSprite;
    private Sprite newBG;

    private int titleX = 0;
    private int titleY = 0;


    // ボタンが押された時の効果音
    private Sound btnPressedSound;

    public HelpScene(MultiSceneActivity context) {
        super(context);
        init();
    }

    @Override
    public void init() {

        spriteMap = new HashMap<String, Sprite>();

        Sprite bg = getBaseActivity().getResourceUtil()
                .getSprite("help_bg.png");
        Sprite helpBg1 = getBaseActivity().getResourceUtil()
                .getSprite("instruction.png");
        Sprite helpBg2 = getBaseActivity().getResourceUtil()
                .getSprite("instruction2.png");
        Sprite helpBg3 = getBaseActivity().getResourceUtil()
                .getSprite("instruction3.png");

        spriteMap.put("TitleBG", bg);
        spriteMap.put("Help1", helpBg1);
        spriteMap.put("Help2", helpBg2);
        spriteMap.put("Help3", helpBg3);
        Sprite storedBG = spriteMap.get("TitleBG");

        if (bg == storedBG){
            System.out.println("correct");
        }

        bg.setPosition(0, 0);
        attachChild(bg);

        titleSprite = getBaseActivity().getResourceUtil().getSprite(
               "help_title.png");

        titleSprite = getBaseActivity().getResourceUtil().getSprite(
                "instruction.png");
        placeToCenterX(titleSprite, 200);
        attachChild(titleSprite);

        // ボタンの追加
        MainActivity mainActivity = (MainActivity)this.getBaseActivity();
        ResourceUtil resourceUtil = mainActivity.getResourceUtil();
        btnStart = resourceUtil.getButtonSprite("help_btn_back.png",
                "help_btn_back_p.png");
 /*
        ButtonSprite btnStart = getBaseActivity().getResourceUtil()
                .getButtonSprite("help_btn_back.png",
                        "help_btn_back_p.png");
*/
        /*
        placeToCenterX(btnStart, 0);        //680
        float sy = titleSprite.getY() + titleSprite.getHeight() - btnStart.getHeight();
        btnStart.setPosition(btnStart.getX(), sy);
        */
        placeToButtonY(titleSprite, btnStart);

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

        this.changeHelpImage("Help1");
        this.changeHelpImage("Help2");
        this.changeHelpImage("Help3");

    }

    // ヘルプ画面の入れ替え
    public void changeHelpImage(String key){
        titleSprite.detachSelf();
        if (newBG != null){
            newBG.detachSelf();
        }
        newBG = spriteMap.get(key);
        placeToCenterX(newBG, 200);
        //newBG.setPosition(titleX, titleY);
        //titleY += 20;
        //titleX += 20;

        attachChild(newBG);
        btnStart.detachSelf();

        placeToButtonY(titleSprite,btnStart);
        attachChild(btnStart);
    }

    public void insertButton(){
        btnStart.detachSelf();

        MainActivity mainActivity = (MainActivity)this.getBaseActivity();
        ResourceUtil resourceUtil = mainActivity.getResourceUtil();
        ButtonSprite btnName = resourceUtil.getButtonSprite("help_btn_back.png",
                "help_btn_back_p.png");

        placeToCenterX(btnName, 200);
        btnName.setPosition(titleX, titleY);
        titleY += 20;
        titleX += 20;

        btnName.setOnClickListener(this);
        attachChild(btnName);

        // ボタンをタップ可能に
        registerTouchArea(btnName);
    }
/*
    // ボタンの追加
    public void insertButton(){
        btnStart.detachSelf();

        MainActivity mainActivity = (MainActivity)this.getBaseActivity();
        ResourceUtil resourceUtil = mainActivity.getResourceUtil();
        btnOK = resourceUtil.getButtonSprite("help_btn_back.png",
                "help_btn_back_p.png");
        btnPrev = resourceUtil.getButtonSprite("help_btn_back.png",
                "help_btn_back_p.png");
        btnNext = resourceUtil.getButtonSprite("help_btn_back.png",
                "help_btn_back_p.png");

        placeToCenterX(btnOK, 200);
        btnOK.setPosition(0, 600);
        placeToCenterX(btnPrev, 200);
        btnPrev.setPosition(150, 600);
        placeToCenterX(btnNext, 200);
        btnNext.setPosition(300, 600);

        btnOK.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        attachChild(btnOK);
        attachChild(btnPrev);
        attachChild(btnNext);

        // ボタンをタップ可能に
        registerTouchArea(btnOK);
        registerTouchArea(btnPrev);
        registerTouchArea(btnNext);
    }
*/
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
