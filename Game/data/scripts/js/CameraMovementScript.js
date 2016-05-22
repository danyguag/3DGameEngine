var CameraImport = Java.type('com.studios.base.engine.framework.game.Camera');
var Physics = Java.type('com.studios.base.engine.framework.components.PhysicsComponent');

var MOVE_SPEED = 15;

var m_cameraComponent;
var m_cameraPhysicsComponent;

var Start = function(CoreEngine, Parent, Graphics)
{
	This = Parent;
	
	m_cameraComponent = This.GetComponent(CameraImport.class);
	m_cameraPhysicsComponent = This.GetComponent(Physics.class);
};


var Update = function(CurrentGameShot)
{
	if (Keyboard.isKeyDown(Keyboard.KEY_W))
		m_cameraPhysicsComponent.Simulate(0, 0, -1, MOVE_SPEED * CurrentGameShot.GetDelta());
	
	if (Keyboard.isKeyDown(Keyboard.KEY_S))
		m_cameraPhysicsComponent.Simulate(0, 0, 1, MOVE_SPEED * CurrentGameShot.GetDelta());
	
	if (Keyboard.isKeyDown(Keyboard.KEY_A))
		m_cameraPhysicsComponent.Simulate(-1, 0, 0, MOVE_SPEED * CurrentGameShot.GetDelta());
	
	if (Keyboard.isKeyDown(Keyboard.KEY_D))
		m_cameraPhysicsComponent.Simulate(1, 0, 0, MOVE_SPEED * CurrentGameShot.GetDelta());
	
	if (Keyboard.isKeyDown(Keyboard.KEY_R))
		m_cameraPhysicsComponent.Simulate(0, 1, 0, MOVE_SPEED * CurrentGameShot.GetDelta());
	
	if (Keyboard.isKeyDown(Keyboard.KEY_F))
		m_cameraPhysicsComponent.Simulate(0, -1, 0, MOVE_SPEED * CurrentGameShot.GetDelta());
	
	if (Keyboard.isKeyDown(Keyboard.KEY_E))
		m_cameraComponent.SetYaw(m_cameraComponent.GetYaw() + (MOVE_SPEED * 5 * CurrentGameShot.GetDelta()));
	
	if (Keyboard.isKeyDown(Keyboard.KEY_Q))
		m_cameraComponent.SetYaw(m_cameraComponent.GetYaw() + -(MOVE_SPEED * 5 * CurrentGameShot.GetDelta()));
	
	if (Keyboard.isKeyDown(Keyboard.KEY_C))
		m_cameraComponent.SetPitch(m_cameraComponent.GetPitch() + (MOVE_SPEED * 5 * CurrentGameShot.GetDelta()));
	
	if (Keyboard.isKeyDown(Keyboard.KEY_Z))
		m_cameraComponent.SetPitch(m_cameraComponent.GetPitch() + -(MOVE_SPEED * 5 * CurrentGameShot.GetDelta()));
};