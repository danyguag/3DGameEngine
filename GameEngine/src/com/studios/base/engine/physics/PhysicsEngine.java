package com.studios.base.engine.physics;

import java.util.HashMap;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.InternalTickCallback;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.studios.base.engine.framework.components.PhysicsComponent;
import com.studios.base.engine.framework.containers.util.EngineMap;
import com.studios.base.engine.framework.debugging.DebugManager;
import com.studios.base.engine.framework.scenegraph.GameObject;

public class PhysicsEngine extends Thread
{
	private EngineMap<GameObject, PhysicsComponent> m_physicsObjects;

	private BroadphaseInterface m_broadInterface;
	private CollisionConfiguration m_physicsConfiguration;
	private Dispatcher m_dispatcher;
	private ConstraintSolver m_solver;
	private DynamicsWorld m_world;
	
	public PhysicsEngine()
	{
		m_physicsObjects = new EngineMap<GameObject, PhysicsComponent>();

		
	}
	
	private DynamicsWorld CreatePhysicsWorld()
	{
		m_broadInterface = new DbvtBroadphase();
		m_physicsConfiguration = new DefaultCollisionConfiguration();
		m_dispatcher = new CollisionDispatcher(m_physicsConfiguration);
		m_solver = new SequentialImpulseConstraintSolver();
		return new DiscreteDynamicsWorld(m_dispatcher, m_broadInterface, m_solver, m_physicsConfiguration);
	}
	
	public void AddPhysicsObject(GameObject obj, PhysicsComponent comp)
	{
		m_physicsObjects.Put(obj, comp);
	}
	
	public PhysicsComponent GetPhysicsObject(GameObject obj)
	{
		return m_physicsObjects.get(obj);
	}
	
	@Override
	public void run()
	{
		m_world = CreatePhysicsWorld();
		
		while (!interrupted())
		{
			m_world.performDiscreteCollisionDetection();
			m_world.setInternalTickCallback(new InternalTickCallback() 
			{
				@Override
				public void internalTick(DynamicsWorld nWorld, float delta) 
				{
					Dispatcher nDispatcher = nWorld.getDispatcher();
					for (int i = 0; i < nDispatcher.getNumManifolds(); ++i)
					{
						PersistentManifold Manifold = nDispatcher.getManifoldByIndexInternal(i);
						RigidBody Body1 = (RigidBody) Manifold.getBody0();
						RigidBody Body2 = (RigidBody) Manifold.getBody1();
						
						CollisionObject PhysicsObj1 = (CollisionObject) Body1.getUserPointer();
						CollisionObject PhysicsObj2 = (CollisionObject) Body1.getUserPointer();
						
						for (int j = 0; j < Manifold.getNumContacts(); ++j)
						{
							ManifoldPoint nContPoint = Manifold.getContactPoint(j);
							
							if (nContPoint.getDistance() < 0.0f)
							{
								DebugManager.Log(getClass().getSimpleName(), "Colliding");
							}
						}
					}
				}
			}, null);
		}
	}
	
	public HashMap<GameObject, PhysicsComponent> GetPhysicsObjects()
	{
		return m_physicsObjects;
	}
}