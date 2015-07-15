package biz.stachibana.ae.cointhrow;

import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;

import android.view.KeyEvent;

public class MainActivity extends MultiSceneActivity {
	// 画面のサイズ。
	private int CAMERA_WIDTH = 480;
	private int CAMERA_HEIGHT = 800;

	public EngineOptions onCreateEngineOptions() {
		// サイズを指定し描画範囲をインスタンス化
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		// ゲームのエンジンを初期化。
		// 第1引数 タイトルバーを表示しないモード
		// 第2引数 画面は縦向き（幅480、高さ800）
		// 第3引数 解像度の縦横比を保ったまま最大まで拡大する
		// 第4引数 描画範囲
		EngineOptions eo = new EngineOptions(true,
				ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(
						CAMERA_WIDTH, CAMERA_HEIGHT), camera);
		// 効果音の使用を許可する
		eo.getAudioOptions().setNeedsSound(true);
		return eo;
	}

	@Override
	protected Scene onCreateScene() {
		// サウンドファイルの格納場所を指定
		SoundFactory.setAssetBasePath("mfx/");
		// InitialSceneをインスタンス化し、エンジンにセット
		InitialScene initialScene = new InitialScene(this);
		// 遷移管理用配列に追加
		getSceneArray().add(initialScene);
		return initialScene;
	}

	@Override
	public void onPause() {
		super.onPause();
		try {
			getResourceUtil().resetAllTexture();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected int getLayoutID() {
		// ActivityのレイアウトのIDを返す
		return R.layout.activity_main;
	}

	@Override
	protected int getRenderSurfaceViewID() {
		// SceneがセットされるViewのIDを返す
		return R.id.renderview;
	}

	@Override
	public void appendScene(KeyListenScene scene) {
		getSceneArray().add(scene);
	}

	@Override
	public void backToInitial() {
		// 遷移管理用配列をクリア
		getSceneArray().clear();
		// 新たにInitialSceneからスタート
		KeyListenScene scene = new InitialScene(this);
		getSceneArray().add(scene);
		getEngine().setScene(scene);
	}

	@Override
	public void refreshRunningScene(KeyListenScene scene) {

	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		// バックボタンが押された時
		if (e.getAction() == KeyEvent.ACTION_DOWN
				&& e.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			// 起動中のSceneのdispatchKeyEvent関数を呼び出し。追加の処理が必要な時はfalseが
			// 返ってくる為、処理
			if (!getSceneArray().get(getSceneArray().size() - 1)
					.dispatchKeyEvent(e)) {
				// Sceneが1つしか起動していない時はゲームを終了
				if (getSceneArray().size() == 1) {
					ResourceUtil.getInstance(this).resetAllTexture();
					finish();
				}
				// 複数のSceneが起動している時は1つ前のシーンへ戻る
				else {
					getEngine().setScene(
							getSceneArray().get(getSceneArray().size() - 2));
					getSceneArray().remove(getSceneArray().size() - 1);
				}
			}
			return true;
		} else if (e.getAction() == KeyEvent.ACTION_DOWN
				&& e.getKeyCode() == KeyEvent.KEYCODE_MENU) {
			getSceneArray().get(getSceneArray().size() - 1).dispatchKeyEvent(e);
			return true;
		}
		return false;
	}
}
