package biz.stachibana.ae.cointhrow;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.BitmapFont;
import org.andengine.util.HorizontalAlign;

import android.content.Intent;
import android.view.KeyEvent;

public class MainScene extends KeyListenScene implements IOnSceneTouchListener,
		ButtonSprite.OnClickListener {

	private static final int MENU_MENU = 1;
	private static final int MENU_TWEET = 2;
	private static final int MENU_RANKING = 3;

	private AnimatedSprite coin;
	// ドラッグ開始座標
	private float[] touchStartPoint;// フリック中か否か
	private boolean isDragging;
	// 画面のタッチ可否
	private boolean isTouchEnabled;
	// コインが飛翔中であるか否か
	private boolean isCoinFlying;
	// コインが飛び出す角度
	private double flyAngle;
	// コインのx座標移動速度
	private float flyXVelocity;
	// コインのy座標移動速度
	private float initialCoinSpeed;
	// コインのy軸進行方向
	private int coinDirection;
	// コインのy軸進行方向が切り替わるy座標
	private int coinUpLimit;
	// コインが缶に入らなかった時、削除し次のコインをセットするy座標
	private int coinDownLimit;
	// 缶
	private Sprite boxSprite;
	// 缶の前面
	private Sprite boxOverlay;
	// 缶の前面Spriteのz-index
	private int zOfBoxOverlay = 1;
	// 衝突判定用Sprite
	private Sprite collisionLine;
	// 左側磁石
	private AnimatedSprite magnetLeft;
	// 右側磁石
	private AnimatedSprite magnetRight;
	// 現在のスコアを表示するテキスト
	private Text currentScoreText;
	// 過去最高のスコアを表示するテキスト
	private Text highScoreText;
	// 現在のスコア
	private int currentScore;
	// 遊び方画面
	private Sprite instructionSprite;
	// 遊び方画面のボタン
	private ButtonSprite instructionBtn;
	// ポーズ中か否か
	private boolean isPaused;
	// ポーズ画面の背景
	private Rectangle pauseBg;
	// サウンド
	private Sound coinInSound;
	private Sound coinBoundSound;
	private Sound coinOutSound;
	private Sound btnPressedSound;

	public MainScene(MultiSceneActivity baseActivity) {
		super(baseActivity);
		init();
	}

	public void init() {
		attachChild(getBaseActivity().getResourceUtil()
				.getSprite("main_bg.png"));

		// 缶のSpriteを追加
		boxSprite = getBaseActivity().getResourceUtil().getSprite(
				"main_box.png");
		placeToCenterX(boxSprite, 180);
		attachChild(boxSprite);

		// 缶の前面Spriteを追加
		boxOverlay = getBaseActivity().getResourceUtil().getSprite(
				"main_box_overlay.png");
		placeToCenterX(boxOverlay, 180);
		// z-indexを設定
		boxOverlay.setZIndex(zOfBoxOverlay);
		// 初期は透明
		boxOverlay.setAlpha(0);
		attachChild(boxOverlay);

		// 衝突判定用Spriteの追加
		collisionLine = getBaseActivity().getResourceUtil().getSprite(
				"main_collision_line.png");
		placeToCenterX(collisionLine, 197);
		collisionLine.setAlpha(0);
		attachChild(collisionLine);

		// スコア表示
		currentScore = 0;
		BitmapFont bitmapFont = new BitmapFont(getBaseActivity()
				.getTextureManager(), getBaseActivity().getAssets(),
				"font/score.fnt");
		bitmapFont.load();

		// ビットマップフォントを元にスコアを表示
		currentScoreText = new Text(20, 20, bitmapFont,
				"Score " + currentScore, 20, new TextOptions(
						HorizontalAlign.LEFT), getBaseActivity()
						.getVertexBufferObjectManager());
		attachChild(currentScoreText);
		highScoreText = new Text(20, 50, bitmapFont, "HighScore "
				+ SPUtil.getInstance(getBaseActivity()).getHighScore(), 20,
				new TextOptions(HorizontalAlign.LEFT), getBaseActivity()
						.getVertexBufferObjectManager());
		attachChild(highScoreText);

		// ハイスコアが0の時(初プレイ時)のみヘルプ画面を出す
		if (SPUtil.getInstance(getBaseActivity()).getHighScore() > 0) {
			// Sceneのタッチリスナーを登録
			setOnSceneTouchListener(this);
			// アップデートハンドラーを登録
			registerUpdateHandler(updateHandler);

			setNewCoin();
		} else {
			showHelp();
		}
	}

	// 遊び方画面を出現させる
	public void showHelp() {
		instructionSprite = ResourceUtil.getInstance(getBaseActivity())
				.getSprite("instruction.png");
		placeToCenter(instructionSprite);
		attachChild(instructionSprite);

		// ボタン
		instructionBtn = ResourceUtil
				.getInstance(getBaseActivity())
				.getButtonSprite("instruction_btn.png", "instruction_btn_p.png");
		placeToCenterX(instructionBtn, 600);
		attachChild(instructionBtn);
		registerTouchArea(instructionBtn);
		instructionBtn.setOnClickListener(new ButtonSprite.OnClickListener() {
			public void onClick(ButtonSprite pButtonSprite,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				instructionSprite.detachSelf();
				instructionBtn.detachSelf();
				unregisterTouchArea(instructionBtn);

				// Sceneのタッチリスナーを登録
				setOnSceneTouchListener(MainScene.this);
				// アップデートハンドラーを登録
				registerUpdateHandler(updateHandler);

				setNewCoin();
			}
		});
	}

	public void setNewCoin() {
		// 古いコインが存在している場合は消去
		if (coin != null) {
			detachChild(coin);
		}

		// フラグ初期値セット
		touchStartPoint = new float[2];
		isTouchEnabled = true;
		isDragging = false;
		isCoinFlying = false;
		// コインのy座標移動の初速を決定
		initialCoinSpeed = 30f;
		// コインの初期y軸進行方向
		coinDirection = 1;
		// コインのy軸進行方向が切り替わるy座標
		coinUpLimit = 80;
		// コインが缶に入らなかった時、削除し次のコインをセットするy座標
		coinDownLimit = 180;
		// 缶の前面Spriteを透明に
		boxOverlay.setAlpha(0);

		// コインをインスタンス化
		coin = getBaseActivity().getResourceUtil().getAnimatedSprite(
				"coin_100.png", 1, 3);
		// x座標を画面中心、y座標を600に設定
		placeToCenterX(coin, 600);

		attachChild(coin);
		// z-indexを反映
		sortChildren();

		// 磁石が設置されていれば削除
		if (magnetLeft != null) {
			magnetLeft.detachSelf();
			magnetLeft = null;
		}
		if (magnetRight != null) {
			magnetRight.detachSelf();
			magnetRight = null;
		}
		// 磁石を左に置くか右に置くかランダムに決定
		int r = (int) (Math.random() * 2);
		if (r == 0) {
			magnetLeft = getBaseActivity().getResourceUtil().getAnimatedSprite(
					"magnet_left.png", 1, 2);
			magnetLeft.setPosition(50, 145);
			// 磁石の大きさを0.3〜1の間でランダムに設定
			float s = (3 + (int) (Math.random() * 7)) / 10.0f;
			magnetLeft.setScale(s);
			// y座標を決定。下から1/3の点がずれないよう調整
			magnetLeft.setY(magnetLeft.getY()
					+ (magnetLeft.getHeight() - magnetLeft.getHeightScaled())
					* 0.33f);
			attachChild(magnetLeft);
		} else {
			magnetRight = getBaseActivity().getResourceUtil()
					.getAnimatedSprite("magnet_right.png", 1, 2);
			magnetRight.setPosition(300, 110);
			float s = (3 + (int) (Math.random() * 7)) / 10.0f;
			magnetRight.setScale(s);
			magnetRight.setY(magnetRight.getY()
					+ (magnetRight.getHeight() - magnetRight.getHeightScaled())
					* 0.33f);
			attachChild(magnetRight);
		}
	}

	@Override
	public void prepareSoundAndMusic() {
		try {
			btnPressedSound = SoundFactory.createSoundFromAsset(
					getBaseActivity().getSoundManager(), getBaseActivity(),
					"clock00.wav");
			coinInSound = SoundFactory.createSoundFromAsset(getBaseActivity()
					.getSoundManager(), getBaseActivity(), "chari07.wav");
			coinBoundSound = SoundFactory.createSoundFromAsset(
					getBaseActivity().getSoundManager(), getBaseActivity(),
					"chari08.wav");
			coinOutSound = SoundFactory.createSoundFromAsset(getBaseActivity()
					.getSoundManager(), getBaseActivity(), "chari13_a.wav");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		if (e.getAction() == KeyEvent.ACTION_DOWN
				&& e.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			// ポーズ中ならポーズ画面を消去
			if (isPaused) {
				// detachChildrenとdetachSelfを同じタイミングで呼ぶ時は別スレッドで
				getBaseActivity().runOnUpdateThread(new Runnable() {
					public void run() {
						for (int i = 0; i < pauseBg.getChildCount(); i++) {
							// 忘れずにタッチの検知を無効に
							unregisterTouchArea((ButtonSprite) pauseBg
									.getChildByIndex(i));
						}
						pauseBg.detachChildren();
						pauseBg.detachSelf();
					}
				});

				isPaused = false;
				isTouchEnabled = true;
				return true;
			} else {
				return false;
			}
		} else if (e.getAction() == KeyEvent.ACTION_DOWN
				&& e.getKeyCode() == KeyEvent.KEYCODE_MENU) {
			// ポーズ中でなければポーズ画面を出す
			if (!isPaused) {
				showMenu();
			}
			return true;
		}
		return false;
	}

	public void showMenu() {
		// 四角形を描画
		pauseBg = new Rectangle(0, 0, getBaseActivity().getEngine().getCamera()
				.getWidth(), getBaseActivity().getEngine().getCamera()
				.getHeight(), getBaseActivity().getVertexBufferObjectManager());
		pauseBg.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		pauseBg.setColor(0, 0, 0);
		pauseBg.setAlpha(0.7f);
		attachChild(pauseBg);

		try {
			ButtonSprite btnMenu = getBaseActivity()
					.getResourceUtil()
					.getButtonSprite("menu_btn_menu.png", "menu_btn_menu_p.png");
			placeToCenterX(btnMenu, 200);
			btnMenu.setTag(MENU_MENU);
			btnMenu.setOnClickListener(this);
			pauseBg.attachChild(btnMenu);
			registerTouchArea(btnMenu);

			ButtonSprite btnTweet = getBaseActivity().getResourceUtil()
					.getButtonSprite("menu_btn_tweet.png",
							"menu_btn_tweet_p.png");
			placeToCenterX(btnTweet, 320);
			btnTweet.setTag(MENU_TWEET);
			btnTweet.setOnClickListener(this);
			pauseBg.attachChild(btnTweet);
			registerTouchArea(btnTweet);

			ButtonSprite btnRanking = getBaseActivity().getResourceUtil()
					.getButtonSprite("menu_btn_ranking.png",
							"menu_btn_ranking_p.png");
			placeToCenterX(btnRanking, 440);
			btnRanking.setTag(MENU_RANKING);
			btnRanking.setOnClickListener(this);
			pauseBg.attachChild(btnRanking);
			registerTouchArea(btnRanking);
		} catch (Exception e) {
			e.printStackTrace();
		}

		isPaused = true;
		isTouchEnabled = false;
	}

	// タッチイベントが発生したら呼ばれる
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// タッチの座標を取得
		float x = pSceneTouchEvent.getX();
		float y = pSceneTouchEvent.getY();
		// 指が触れた瞬間のイベント。タッチの座標がコイン上であるかどうかチェック
		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN
				&& isTouchEnabled
				&& (x > coin.getX() && x < coin.getX() + coin.getWidth())
				&& (y > coin.getY() && y < coin.getY() + coin.getHeight())) {

			// フラグ
			isTouchEnabled = false;
			isDragging = true;

			// 開始点を登録
			touchStartPoint[0] = x;
			touchStartPoint[1] = y;
		}
		// 指が離れた時、何らかの原因でタッチ処理が中断した場合のイベント
		else if ((pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP || pSceneTouchEvent
				.getAction() == TouchEvent.ACTION_CANCEL) && isDragging) {
			// 終点を登録
			float[] touchEndPoint = new float[2];
			touchEndPoint[0] = x;
			touchEndPoint[1] = y;

			// フリックの距離が短すぎる時にはフリックと判定しない
			if ((touchEndPoint[0] - touchStartPoint[0] < 50 && touchEndPoint[0]
					- touchStartPoint[0] > -50)
					&& (touchEndPoint[1] - touchStartPoint[1] < 50 && touchEndPoint[1]
							- touchStartPoint[1] > -50)) {
				isTouchEnabled = true;
				isDragging = false;
				return true;
			}

			// フリックの角度を求める
			flyAngle = getAngleByTwoPosition(touchStartPoint, touchEndPoint);
			// 下から上へのフリックを0°に調整。
			flyAngle -= 180;
			// フリックの角度が前向きで無い時はフリックを無効に
			if (flyAngle < -80 || flyAngle > 80) {
				isTouchEnabled = true;
				isDragging = false;
				return true;
			}

			// コインのx座標移動速度を調整
			flyXVelocity = (float) (flyAngle / 10.0f);
			// フラグをONに
			isCoinFlying = true;
			// アニメーション開始
			coin.animate(50);
			// コインの角度をランダムに設定
			coin.setRotation((int) (Math.random() * 360));
			// 磁石が設置されていればアニメーション開始
			if (magnetLeft != null) {
				magnetLeft.animate(100);
			}
			if (magnetRight != null) {
				magnetRight.animate(100);
			}
		}

		return true;
	}

	// アップデートハンドラ。1秒間に60回呼び出される
	public TimerHandler updateHandler = new TimerHandler(1f / 60f, true,
			new ITimerCallback() {

				public void onTimePassed(TimerHandler pTimerHandler) {
					// コインが飛んでいるなら実行
					if (isCoinFlying) {
						// コインが上向きに飛んでいる時
						if (coinDirection == 1) {
							// コインのy座標移動速度が3以上の時
							if (initialCoinSpeed > 3.0f) {
								// 速度を徐々に落とす
								initialCoinSpeed *= 0.96f;
							}
							// コインを徐々に小さく
							coin.setScale(coin.getScaleX() * 0.97f);
							// コインのy座標がリミットに達したら
							if (coin.getY() < coinUpLimit) {
								// コインのy軸進行方向を逆向きに
								coinDirection = -coinDirection;
							}
							// コインが下向きに飛んでいる時
						} else {
							// 缶の前面Spriteを不透明に
							boxOverlay.setAlpha(1);
							// コインの座標、大きさの短径を作成。スケールしたSpriteは都度計算する必要がある
							Rectangle scaledSprite = new Rectangle(
									coin.getX()
											+ ((coin.getWidth() - coin
													.getWidthScaled()) / 2.0f),
									coin.getY()
											+ ((coin.getHeight() - coin
													.getHeightScaled()) / 2.0f),
									coin.getWidthScaled(), coin
											.getHeightScaled(),
									getBaseActivity()
											.getVertexBufferObjectManager());

							// 衝突判定
							if (scaledSprite.collidesWith(collisionLine)) {
								// 衝突した瞬間のコインの中心のx座標を取得
								float collideX = scaledSprite.getX()
										+ scaledSprite.getWidth() / 2.0f;

								// collisionLine左端に接触した時
								if (collideX < collisionLine.getX()
										+ scaledSprite.getWidth() / 2.0f) {
									// 効果音を再生
									coinBoundSound.play();
									// コインの進行方向を上向きに
									coinDirection = 1;
									// コインの右側が縁に接触した時
									if (collideX < collisionLine.getX()) {
										// 接触が厚い程すると上に跳ね、浅い程左右に跳ねる
										flyXVelocity = -10.0f
												* ((collisionLine.getX() - collideX) / (scaledSprite
														.getWidth() / 2.0f));
										coinUpLimit += 50 * (1 - ((collisionLine
												.getX() - collideX) / (scaledSprite
												.getWidth() / 2.0f)));
									}
									// コインの左側が縁に接触した時
									else {
										// 接触が厚い程すると上に跳ね、浅い程左右に跳ねる
										flyXVelocity = 10.0f * ((collideX - collisionLine
												.getX()) / (scaledSprite
												.getWidth() / 2.0f));
										coinUpLimit += 50 * (1 - ((collideX - collisionLine
												.getX()) / (scaledSprite
												.getWidth() / 2.0f)));
									}
								}
								// collisionLine右端に接触した時
								else if (collideX > collisionLine.getX()
										+ collisionLine.getWidth()
										- scaledSprite.getWidth() / 2.0f) {
									// 効果音を再生
									coinBoundSound.play();
									// コインの進行方向を上向きに
									coinDirection = 1;
									// コインの右側が縁に接触した時
									if (collideX < collisionLine.getX()
											+ collisionLine.getWidth()) {
										// 接触が厚い程すると上に跳ね、浅い程左右に跳ねる
										flyXVelocity = -10.0f
												* (((collisionLine.getX() + collisionLine
														.getWidth()) - collideX) / (scaledSprite
														.getWidth() / 2.0f));
										coinUpLimit += 50 * (1 - (((collisionLine
												.getX() + collisionLine
												.getWidth()) - collideX) / (scaledSprite
												.getWidth() / 2.0f)));
									}
									// コインの左側が縁に接触した時
									else {
										// 接触が厚い程すると上に跳ね、浅い程左右に跳ねる
										flyXVelocity = 10.0f * ((collideX - (collisionLine
												.getX() + collisionLine
												.getWidth())) / (scaledSprite
												.getWidth() / 2.0f));
										coinUpLimit += 50 * (1 - ((collideX - (collisionLine
												.getX() + collisionLine
												.getWidth())) / (scaledSprite
												.getWidth() / 2.0f)));
									}
								} else {
									// 効果音を再生
									coinInSound.play();
									// スコアをインクリメント
									currentScore++;
									// スコアをセット
									currentScoreText.setText("Score "
											+ currentScore);

									// ハイスコア更新時はハイスコアのテキストも更新
									if (currentScore > SPUtil.getInstance(
											getBaseActivity()).getHighScore()) {
										SPUtil.getInstance(getBaseActivity())
												.setHighScore(currentScore);
										highScoreText.setText("Highscore "
												+ SPUtil.getInstance(
														getBaseActivity())
														.getHighScore());

										// ハイスコア更新時は派手なアクションでユーザーにフィードバック
										highScoreText
												.registerEntityModifier(new LoopEntityModifier(
														new SequenceEntityModifier(
																new ScaleModifier(
																		0.2f,
																		1.0f,
																		1.4f),
																new ScaleModifier(
																		0.2f,
																		1.4f,
																		1.0f)),
														2));
									}

									coinDirection = 0;
									coin.stopAnimation();
									setNewCoin();
									return;
								}
							}
							// y軸移動スピードを徐々に速く
							initialCoinSpeed *= 1.05f;
							// コインのy座標がリミットに達したら
							if (coin.getY() > coinDownLimit) {
								// 効果音を再生
								coinOutSound.play();

								// スコアを0に
								currentScore = 0;
								// スコアをセット
								currentScoreText.setText("Score "
										+ currentScore);
								// コインの移動をストップ
								isCoinFlying = false;
								// コインのy軸移動方向を初期値に
								coinDirection = 1;
								// コインのアニメーションをストップ
								coin.stopAnimation();
								// 新しいコインをセット
								setNewCoin();
								return;
							}
						}
						// x座標の移動
						coin.setX(coin.getX() + flyXVelocity);
						// xの移動量を徐々に大きく
						flyXVelocity *= 1.03f;
						// y座標の移動
						coin.setY(coin.getY() - initialCoinSpeed
								* coinDirection);

						// 磁石に引き寄せられる動き
						if (magnetLeft != null) {
							if (coin.getY() < 430) {
								flyXVelocity -= 0.65f * magnetLeft.getScaleX();
							}
						} else if (magnetRight != null) {
							if (coin.getY() < 430) {
								flyXVelocity += 0.65f * magnetRight.getScaleX();
							}
						}
					}
				}
			});

	// 2点間の角度を求める公式
	private double getAngleByTwoPosition(float[] start, float[] end) {
		double result = 0;

		float xDistance = end[0] - start[0];
		float yDistance = end[1] - start[1];

		result = Math.atan2((double) yDistance, (double) xDistance) * 180
				/ Math.PI;
		result += 270;

		return result;
	}

	public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {
		// 効果音を再生
		btnPressedSound.play();
		switch (pButtonSprite.getTag()) {
		case MENU_MENU:
			getBaseActivity().backToInitial();
			break;
		case MENU_TWEET:
			Intent sendIntent = new Intent(Intent.ACTION_SEND);
			sendIntent.setType("text/plain");
			sendIntent.putExtra(Intent.EXTRA_TEXT, "ハイスコア"
					+ SPUtil.getInstance(getBaseActivity()).getHighScore()
					+ "点！ハマる！Androidゲーム「銭投の達人」にあなたも挑戦 → http://stachibana.biz");
			getBaseActivity().startActivity(sendIntent);
			break;
		case MENU_RANKING:
			break;
		}
	}
}
