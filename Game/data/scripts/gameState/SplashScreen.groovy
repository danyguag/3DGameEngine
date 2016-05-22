import com.studios.base.engine.core.Engine
import org.lwjgl.util.vector.Vector2f
import com.studios.base.engine.rendering.ui.UI

import static com.studios.ag.SplashScreen.*

void Start()
{
    presents = UI.CreateUI("ui/inventory/Inventory.png", new Vector2f(1, 2), new Vector2f(0.5f, 0.5f));
}

public void Update()
{

}

public void Render() {  }

public void Stop() {  }

public void CleanUp() {  }

this as Engine