var PlayerImport = Java.type('com.studios.base.engine.framework.components.Player');
var Physics = Java.type('com.studios.base.engine.framework.components.PhysicsComponent');

var IsMoved;
var m_moveSpeed;
var m_physicsComponent;

var Start = function(CoreEngine, Parent, Graphics) 
{
	This = Parent;
	m_physicsComponent = This.GetComponent(Physics.class);
	IsMoved = false;
	m_moveSpeed = 15;
};

var Update = function(CurrentGameShot) 
{
	var MoveAmount = m_moveSpeed * CurrentGameShot.GetDelta();

	if (Keyboard.isKeyDown(Keyboard.KEY_J))
    {
    	m_physicsComponent.Simulate(-1, 0, 0, MoveAmount);
    	IsMoved = true;
    }
    if (Keyboard.isKeyDown(Keyboard.KEY_K))
    {
		m_physicsComponent.Simulate(1, 0, 0, MoveAmount);
		IsMoved = true;
	}

	if (IsMoved)
	{
		IsMoved = false;
	}
};