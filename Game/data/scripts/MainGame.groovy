import com.studios.base.engine.components.MeshComponent
import com.studios.base.engine.core.Engine
import com.studios.base.engine.core.GameObject
import com.studios.base.engine.core.Game
import com.studios.base.engine.core.util.RenderUtil
import com.studios.base.engine.rendering.Camera
import com.studios.base.engine.rendering.entity.moving.Player
import com.studios.base.engine.rendering.entity.EntityManager
import com.studios.base.engine.rendering.model.Model
import com.studios.base.engine.rendering.textures.Texture
import org.lwjgl.util.vector.Vector3f

void Start()
{
   /*Game.player = new Player(new Model(RenderUtil.data,RenderUtil.texCoords,RenderUtil.indices),
    						 new Vector3f(0,0,0),new Vector3f(0,0,0),new Vector3f(1, 1, 1), "player")
    GameObject gameObject = new GameObject()
    gameObject.AddComponent(new MeshComponent(Game.player, new Texture("textures/Splash_Screen_Presents.png")))
    Game.AddObject(gameObject)
    GameObject cameraObject = new GameObject()
    cameraObject.AddComponent(Game.camera)
    Game.AddObject(cameraObject)
    def testEntity = EntityManager.Get("player");*/
}

public void Update()
{

}

public void Stop() {  }

public void CleanUp() {  }

this as Engine