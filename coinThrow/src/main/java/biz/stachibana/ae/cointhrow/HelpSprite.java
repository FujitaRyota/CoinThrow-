package biz.stachibana.ae.cointhrow;

import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.sprite.vbo.HighPerformanceTiledSpriteVertexBufferObject;
import org.andengine.entity.sprite.vbo.ITiledSpriteVertexBufferObject;
import org.andengine.opengl.shader.PositionColorTextureCoordinatesShaderProgram;
import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.DrawType;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

/**
 * Created by sys314 on 2015/08/26.
 */
public class HelpSprite extends TiledSprite {
    // ===========================================================
    // Constructors
    // ===========================================================

    public HelpSprite(final float pX, final float pY, final ITiledTextureRegion pTiledTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager) {
        this(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager, DrawType.STATIC);
    }

    public HelpSprite(final float pX, final float pY, final ITiledTextureRegion pTiledTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager, final ShaderProgram pShaderProgram) {
        this(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager, DrawType.STATIC, pShaderProgram);
    }

    public HelpSprite(final float pX, final float pY, final ITiledTextureRegion pTiledTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager, final DrawType pDrawType) {
        this(pX, pY, pTiledTextureRegion.getWidth(), pTiledTextureRegion.getHeight(), pTiledTextureRegion, pVertexBufferObjectManager, pDrawType);
    }

    public HelpSprite(final float pX, final float pY, final ITiledTextureRegion pTiledTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager, final DrawType pDrawType, final ShaderProgram pShaderProgram) {
        this(pX, pY, pTiledTextureRegion.getWidth(), pTiledTextureRegion.getHeight(), pTiledTextureRegion, pVertexBufferObjectManager, pDrawType, pShaderProgram);
    }

    public HelpSprite(final float pX, final float pY, final ITiledTextureRegion pTiledTextureRegion, final ITiledSpriteVertexBufferObject pHelpSpriteVertexBufferObject) {
        this(pX, pY, pTiledTextureRegion.getWidth(), pTiledTextureRegion.getHeight(), pTiledTextureRegion, pHelpSpriteVertexBufferObject);
    }

    public HelpSprite(final float pX, final float pY, final ITiledTextureRegion pTiledTextureRegion, final ITiledSpriteVertexBufferObject pHelpSpriteVertexBufferObject, final ShaderProgram pShaderProgram) {
        this(pX, pY, pTiledTextureRegion.getWidth(), pTiledTextureRegion.getHeight(), pTiledTextureRegion, pHelpSpriteVertexBufferObject, pShaderProgram);
    }

    public HelpSprite(final float pX, final float pY, final float pWidth, final float pHeight, final ITiledTextureRegion pTiledTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager) {
        this(pX, pY, pWidth, pHeight, pTiledTextureRegion, pVertexBufferObjectManager, DrawType.STATIC);
    }

    public HelpSprite(final float pX, final float pY, final float pWidth, final float pHeight, final ITiledTextureRegion pTiledTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager, final ShaderProgram pShaderProgram) {
        this(pX, pY, pWidth, pHeight, pTiledTextureRegion, pVertexBufferObjectManager, DrawType.STATIC, pShaderProgram);
    }

    public HelpSprite(final float pX, final float pY, final float pWidth, final float pHeight, final ITiledTextureRegion pTiledTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager, final DrawType pDrawType) {
        this(pX, pY, pWidth, pHeight, pTiledTextureRegion, new HighPerformanceTiledSpriteVertexBufferObject(pVertexBufferObjectManager, HelpSprite.TILEDSPRITE_SIZE * pTiledTextureRegion.getTileCount(), pDrawType, true, Sprite.VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT));
    }

    public HelpSprite(final float pX, final float pY, final float pWidth, final float pHeight, final ITiledTextureRegion pTiledTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager, final DrawType pDrawType, final ShaderProgram pShaderProgram) {
        this(pX, pY, pWidth, pHeight, pTiledTextureRegion, new HighPerformanceTiledSpriteVertexBufferObject(pVertexBufferObjectManager, HelpSprite.TILEDSPRITE_SIZE * pTiledTextureRegion.getTileCount(), pDrawType, true, Sprite.VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT), pShaderProgram);
    }

    public HelpSprite(final float pX, final float pY, final float pWidth, final float pHeight, final ITiledTextureRegion pTiledTextureRegion, final ITiledSpriteVertexBufferObject pHelpSpriteVertexBufferObject) {
        this(pX, pY, pWidth, pHeight, pTiledTextureRegion, pHelpSpriteVertexBufferObject, PositionColorTextureCoordinatesShaderProgram.getInstance());
    }

    public HelpSprite(final float pX, final float pY, final float pWidth, final float pHeight, final ITiledTextureRegion pTiledTextureRegion, final ITiledSpriteVertexBufferObject pHelpSpriteVertexBufferObject, final ShaderProgram pShaderProgram) {
        super(pX, pY, pWidth, pHeight, pTiledTextureRegion, pHelpSpriteVertexBufferObject, pShaderProgram);

        //this.mTiledSpriteVertexBufferObject = pHelpSpriteVertexBufferObject;
    }

}
