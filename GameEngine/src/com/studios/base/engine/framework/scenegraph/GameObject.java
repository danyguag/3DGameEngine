package com.studios.base.engine.framework.scenegraph;

import org.json.JSONArray;
import org.json.JSONObject;

import com.studios.base.engine.core.CoreEngine;
import com.studios.base.engine.core.CoresManager;
import com.studios.base.engine.framework.components.Entity;
import com.studios.base.engine.framework.components.GameComponent;
import com.studios.base.engine.framework.components.JSON;
import com.studios.base.engine.framework.components.PhysicsComponent;
import com.studios.base.engine.framework.components.Player;
import com.studios.base.engine.framework.components.UIComponent;
import com.studios.base.engine.framework.containers.util.EngineList;
import com.studios.base.engine.framework.containers.util.EngineMap;
import com.studios.base.engine.framework.containers.util.EngineTree;
import com.studios.base.engine.framework.debugging.DebugManager;
import com.studios.base.engine.framework.game.Camera;
import com.studios.base.engine.framework.game.Game;
import com.studios.base.engine.framework.graphics.Graphics;
import com.studios.base.engine.framework.math.Scale2f;
import com.studios.base.engine.framework.math.Vector2f;
import com.studios.base.engine.framework.threading.Snapshot;
import com.studios.base.engine.framework.transform.Transform;
import com.studios.base.engine.framework.transform.Transform2f;
import com.studios.base.engine.framework.transform.Transform3f;
import com.studios.base.engine.framework.transform.TransformType;
import com.studios.base.engine.framework.ui.ButtonComponent;
import com.studios.base.engine.rendering.MainFrameBufferRenderingEngine;
import com.studios.base.engine.rendering.shader.Shader;
import com.studios.base.engine.script.EngineScript;
import com.studios.base.engine.script.JSScript;
import com.studios.base.engine.script.ScriptManager;
import com.studios.base.engine.script.ScriptType;

public class GameObject
{
    private EngineTree<GameObject> m_children;
    private EngineList<GameComponent> m_components;
    private EngineMap<TransformType, Transform> m_transforms;
    private Graphics m_graphics;
    private CoreEngine m_coreEngine;
    private TransformType m_lastAddedType;
    private EngineMap<ScriptType, ScriptManager> m_scripts;
    private String m_objectName;
    private boolean m_initialized;
    
    public GameObject(String Name)
    {
    	m_initialized = false;
    	m_objectName = Name;
    	
    	m_transforms = new EngineMap<TransformType, Transform>();
    	
        m_children   = new EngineTree<GameObject>(this);
        m_components = new EngineList<GameComponent>();

        m_scripts = new EngineMap<ScriptType, ScriptManager>();
    }

    public void InitAll(MainFrameBufferRenderingEngine nRenderingEngine)
    {
    	m_initialized = true;
    	GetGraphics().SetRenderingEngine(nRenderingEngine);
    	
    	Init();
    	
    	
    	for (EngineTree<GameObject> Branch : m_children.GetTree())
    		Branch.GetSelf().InitAll(nRenderingEngine);    	
    }
    
    private void Init()
    {
    	for (GameComponent comp : m_components)
    		comp.Init();
    	
    	for (ScriptManager Manager : m_scripts.GetValues())
    		Manager.Start(m_coreEngine, this, GetGraphics());
    }
    
    public void UpdateAll(Snapshot CurrentGameShot)
    {
    	Update(CurrentGameShot);
    	
    	for (EngineTree<GameObject> Branch : m_children.GetTree())
    		Branch.GetSelf().UpdateAll(CurrentGameShot);
    }
    
    public void RenderAll3D(int CurrentRenderPass, Shader ForwardRenderShader)
    {
    	GetGraphics().Render3D(CurrentRenderPass, ForwardRenderShader);
    	
    	for (EngineTree<GameObject> Branch : m_children.GetTree())
    		Branch.GetSelf().GetGraphics().Render3D(CurrentRenderPass, ForwardRenderShader);
    }
    
    public void RenderAll2D(Shader LineShader, Shader UIShader, Shader FontShader)
    {
    	GetGraphics().Render2D(LineShader, UIShader, FontShader);
    	
    	for (EngineTree<GameObject> Branch : m_children.GetTree())
    		Branch.GetSelf().GetGraphics().Render2D(LineShader, UIShader, FontShader);
    }
    
    public void Update(Snapshot CurrentGameShot)
    {
        for (Transform nTransform : m_transforms.values())
        	nTransform.Update();
        	
        for (GameComponent gameComponent : m_components)
        {
        	if (gameComponent instanceof PhysicsComponent)
            {
            	PhysicsComponent physics = (PhysicsComponent) gameComponent;
            	if (!CoresManager.Physics.GetPhysicsObjects().containsKey(physics))
            	{
            		CoresManager.Physics.AddPhysicsObject(this, physics);
            	}
            }
        	
            gameComponent.Update(CurrentGameShot);
        }
    	for (ScriptManager Manager : m_scripts.GetValues())
    		Manager.Update(CurrentGameShot);
    }

    public void AddChild(GameObject Child)
    {
        m_children.AddBranch(Child.GetChildren());
        Child.SetCoreEngine(m_coreEngine);
        if (m_initialized && Child.IsInitialized())
        {
        	Child.InitAll(GetGraphics().GetRenderingEngine());
        }
    }
    
    public <T extends GameComponent> T GetComponent(Class<T> Type)
    {
    	for (GameComponent Component : m_components)
    	{
    		if (Type.isInstance(Component))
    			return Type.cast(Component);
    	}
    	throw new IllegalStateException("No component Found in: " + GetName());
    }
    
    public GameObject GetChildByName(String Name)
    {
    	for (EngineTree<GameObject> Child : m_children.GetTree())
    		if (Child.GetSelf().GetName().equals(Name))
    			return Child.GetSelf();
    	return null;
    }
    
    public void AddJSON(JSON json)
    {
    	LoadJsonGameObjects(json);
    }

    public boolean IsInitialized()
    {
    	return m_initialized;
    }
    
    public void AddComponent(GameComponent gameComponent)
    {
    	AddTransform(gameComponent.GetTransformType());
    	boolean Add = false;
    	int Count = 0;
    	
    	if (m_components.size() >= 1)
    	{
    		for (GameComponent comp : m_components)
    		{
    			if (comp instanceof PhysicsComponent)
    			{
    				Count++;
    			}
    		}
    	}
    	
    	if (Count == 1 | Count == 0)
    	{
    		Add = true;
    	}
    	
        if (Add)
        {
        	m_components.Add(gameComponent);
            gameComponent.SetParent(this);
        }
    }

    public EngineTree<GameObject> GetChildren()
    {
        return m_children;
    }

    public EngineList<GameComponent> GetComponents()
    {
        return m_components;
    }
    
    public Graphics GetGraphics()
    {
    	if (m_graphics == null)
    		m_graphics = new Graphics();
    	return m_graphics;
    }
    
    public void SetCoreEngine(CoreEngine nCoreEngine)
    {
    	m_coreEngine = nCoreEngine;
    	
    	for (EngineTree<GameObject> Branch : m_children.GetTree())
    		Branch.GetSelf().SetCoreEngine(nCoreEngine);
    }
    
    public CoreEngine GetCoreEngine()
    {
    	return (m_coreEngine == null) ? null : m_coreEngine;
    }
    
    public Transform AddTransform(TransformType Type)
    {
    	Transform Result = null;
    	if (m_lastAddedType != null)
    	{
    		switch (m_lastAddedType)
    		{
    		case TRANSFROM_2F:
    		{
    			if (Type == m_lastAddedType)
    				return Result;
    			else if (Type == TransformType.TRANSFROM_3F)
    			{
    				Result = new Transform3f();
    				m_transforms.Put(TransformType.TRANSFROM_3F, Result);
    			}
    		}break;
    		case TRANSFROM_3F:
    		{
    			if (Type == m_lastAddedType)
    				return Result;
    			else if (Type == TransformType.TRANSFROM_2F)
    			{
    				Result = new Transform2f();
    				m_transforms.Put(TransformType.TRANSFROM_2F, Result);
    			}
    			
    		}break;
    		case NEITHER:
    		{
    			m_lastAddedType = null;
    		}break;
    		}
    	}
    	switch (Type)
    	{
    	case TRANSFROM_2F:
    	{
    		Result = new Transform2f();
    		m_transforms.Put(Type, Result);
    	}break;
    	case TRANSFROM_3F:
    	{
    		Result = new Transform3f();
    		m_transforms.Put(Type, Result);
    	}break;
    	}
    	
    	m_lastAddedType = Type;
    	return Result;
    }
    
    public Transform3f GetTransform3f()
    {
    	if (!GetTransforms().containsKey(TransformType.TRANSFROM_3F))
    		GetTransforms().Put(TransformType.TRANSFROM_3F, new Transform3f());
    	return (Transform3f) GetTransforms().get(TransformType.TRANSFROM_3F); 
    }
    
    public Transform2f GetTransform2f()
    {
    	if (!GetTransforms().containsKey(TransformType.TRANSFROM_2F))
    		GetTransforms().Put(TransformType.TRANSFROM_2F, new Transform2f());
    	return (Transform2f) GetTransforms().get(TransformType.TRANSFROM_2F); 
    }
    
    public EngineMap<TransformType, Transform> GetTransforms()
    {
    	return m_transforms;
    }
    
    public String GetName()
    {
    	return m_objectName;
    }
    
    public void AddScript(EngineScript Script, Class Interface)
    {
    	if (m_scripts.get(Script.GetType()) == null)
    		m_scripts.put(Script.GetType(), new ScriptManager(Script.GetType()));
    	m_scripts.get(Script.GetType()).AddScript(Script, Interface);
    }
    
    public EngineMap<ScriptType, ScriptManager> GetScriptManagers()
    {
    	return m_scripts;
    }
    
    public void LoadJsonGameObjects(JSON File)
    {
    	for (String NameOfGameObject : File.GetAllObjects())
    	{
    		DebugManager.Log(getClass().getSimpleName(), NameOfGameObject);
    		JSONObject Child = (JSONObject) File.Get(NameOfGameObject);
        	String ObjectName = Child.getString("Name");
            File.GetAllObjects();    	
        	GameObject ChildObj = new GameObject(ObjectName);
        	
        	String CompTransformType = Child.getString("TransformType");
        	
        	switch(CompTransformType)
        	{
        	case "Both":
        	{
        		AddTransform(TransformType.TRANSFROM_2F);
        		AddTransform(TransformType.TRANSFROM_3F);
        	}break;
        	case "Transform2f":
        	{
        		AddTransform(TransformType.TRANSFROM_2F);
        	}break;
        	case "Transform3f":
        	{
        		AddTransform(TransformType.TRANSFROM_3F);
        	}break;
        	case "Neither":
        	{					
        	}break;
        	default:
        		throw new IllegalStateException("You have not selected a valid TransformType: " + CompTransformType);
        	}
    				
        	Integer ComponentsCount = (Integer) Child.get("ComponentsCount");
    				
        	if (ComponentsCount > 0)
        	{
        		for (int i = 1; i < ComponentsCount + 1; i++)
        		{
        			JSONObject Component = 	
        					(JSONObject) Child.get("GameComponent" + i);
    						
        			String Type = Component.getString("Type");
    						
        			switch (Type)
        			{
        			case "Entity":
        			{
        				Entity entity = new Entity((String) Component.getString("Model"), (String)Component.getString("Texture"));
        				ChildObj.AddComponent(entity);
    						}break;
        			case "Player":
        			{
        				Player player = new Player(Component.getString("Model"), 
        						Component.getString("Texture"),
        						Component.getString("Username"));
        				ChildObj.AddComponent(player);
        			}break;
        			case "PhysicsComponent":
        			{
        				ChildObj.AddComponent(new PhysicsComponent());
        			}break;
        			case "Terrain":
        			{
    							
        			}break;
        			case "UI":
        			{
        				UIComponent ui = new UIComponent(Component.getString("Texture"), 
        						new Vector2f((float)Component.getDouble("PositionX"), (float)Component.getDouble("PositionY")),
        						new Scale2f((float)Component.getDouble("ScaleX"), (float)Component.getDouble("ScaleY")));
        				ChildObj.AddComponent(ui);
        			}break;
        			case "Particle":
        			{
        				
        			}break;
        			case "Animation":
        			{
    							
        			}break;
        			case "Camera":
        			{
        				Game.camera = new Camera();
        				ChildObj.AddComponent(Game.camera);
        			}break;
        			case "JSON":
        			{
        				JSONArray JSONs = Component.getJSONArray("JSON");
        				
        				for (int j = 0; j < JSONs.length(); j++)
        				{
        					LoadJsonGameObjects(new JSON(JSONs.getString(j)));
        				}
        			}break;
        			case "Button":
        			{
        				ChildObj.AddComponent(new ButtonComponent(
        						Component.getString("Texture"),
        						new Vector2f(
        								(float) Component.getDouble("PositionX"),
        								(float) Component.getDouble("PositionY")
        								),
        						new Scale2f(
        								(float) Component.getDouble("ScaleX"),
        								(float) Component.getDouble("ScaleY"))));
        			}break;
        			default:
        				throw new IllegalStateException("Illegal ComponentType: " + Type);
        			}
        			String AllNames = Component.names().toString();
        			
    				if (AllNames.contains("Transform3f.PositionX"))
    				{
    					ChildObj.GetTransform3f().SetPositionX((float) Component.getDouble("Transform3f.PositionX"));
    				}
    				if (AllNames.contains("Transform3f.PositionY"))
    				{
    					ChildObj.GetTransform3f().SetPositionY((float) Component.getDouble("Transform3f.PositionY"));
    				}
    				if (AllNames.contains("Transform3f.PositionZ"))
    				{
    					ChildObj.GetTransform3f().SetPositionZ((float) Component.getDouble("Transform3f.PositionZ"));
    				}
    				
    				if (AllNames.contains("Transform3f.RotationX"))
    				{
    					ChildObj.GetTransform3f().SetRotationX((float) Component.getDouble("Transform3f.RotationX"));
    				}
    				if (AllNames.contains("Transform3f.RotationY"))
    				{
    					ChildObj.GetTransform3f().SetRotationY((float) Component.getDouble("Transform3f.RotationY"));
    				}
    				if (AllNames.contains("Transform3f.RotationZ"))
    				{
    					ChildObj.GetTransform3f().SetRotationZ((float) Component.getDouble("Transform3f.RotationZ"));
    				}
    				
    				if (AllNames.contains("Transform3f.ScaleX"))
    				{
    					ChildObj.GetTransform3f().SetScaleX((float) Component.getDouble("Transform3f.ScaleX"));
    				}
    				if (AllNames.contains("Transform3f.ScaleY"))
    				{
    					ChildObj.GetTransform3f().SetScaleY((float) Component.getDouble("Transform3f.ScaleY"));
    				}
    				if (AllNames.contains("Transform3f.ScaleZ"))
    				{
    					ChildObj.GetTransform3f().SetScaleZ((float) Component.getDouble("Transform3f.ScaleZ"));
    				}
    				
    				if (AllNames.contains("Transform2f.PositionX"))
    				{
    					ChildObj.GetTransform2f().SetPositionX((float) Component.getDouble("Transform2f.PositionX"));
    				}
    				if (AllNames.contains("Transform2f.PositionY"))
    				{
    					ChildObj.GetTransform2f().SetPositionY((float) Component.getDouble("Transform2f.PositionY"));
    				}						
    				if (AllNames.contains("Transform2f.RotationX"))
    				{
    					ChildObj.GetTransform2f().SetRotationX((float) Component.getDouble("Transform2f.RotationX"));
    				}
    				if (AllNames.contains("Transform2f.RotationY"))
    				{
    					ChildObj.GetTransform2f().SetRotationY((float) Component.getDouble("Transform2f.RotationY"));
    				}						
    				if (AllNames.contains("Transform2f.ScaleX"))
    				{
    					GetTransform2f().SetScaleX((float) Component.getDouble("Transform2f.ScaleX"));
    				}
    				if (AllNames.contains("Transform2f.ScaleY"))
    				{
    					ChildObj.GetTransform2f().SetScaleY((float) Component.getDouble("Transform2f.ScaleY"));
    				}
    			}
    		}
        	AddChild(ChildObj);
    	}
    }
}
