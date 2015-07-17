package biz.stachibana.ae.cointhrow;

import java.io.IOException;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;

import android.view.KeyEvent;

public class InitialScene extends KeyListenScene implements
		ButtonSprite.OnClickListener {
	private static final int INITIAL_START = 1;
	private static final int INITIAL_RANKING = 2;
	private static final int INITIAL_RECOMMEND = 3;

	// ボタンが押された時の効果音
	private Sound btnPressedSound;

	public InitialScene(MultiSceneActivity context) {
		super(context);
		init();
	}

	@Override
	public void init() {

		Sprite bg = getBaseActivity().getResourceUtil()
				.getSprite("main_bg.png");
		bg.setPosition(0, 0);
		attachChild(bg);

		Sprite titleSprite = getBaseActivity().getResourceUtil().getSprite(
				"initial_title.png");
		placeToCenterX(titleSprite, 80);
		attachChild(titleSprite);

		// ボタンの追加
		ButtonSprite btnStart = getBaseActivity().getResourceUtil()
				.getButtonSprite("initial_btn_start.png",
						"initial_btn_start_p.png");
		placeToCenterX(btnStart, 480);
		btnStart.setTag(INITIAL_START);
		btnStart.setOnClickListener(this);
		attachChild(btnStart);
		// ボタンをタップ可能に
		registerTouchArea(btnStart);

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
		case INITIAL_START:
			// リソースの解放
			ResourceUtil.getInstance(getBaseActivity()).resetAllTexture();
			// MainSceneへ移動
			getBaseActivity().getEngine().setScene(scene);
			// 遷移管理用配列に追加
			getBaseActivity().appendScene(scene);
			break;
		case INITIAL_RANKING:
			break;
		case INITIAL_RECOMMEND:
			// ヘルプ呼出し
			HelpScene helpScene = new HelpScene(getBaseActivity());
			getBaseActivity().getEngine().setScene(helpScene);
			break;
		}
	}
}
