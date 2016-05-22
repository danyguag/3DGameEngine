package com.studios.base.engine.framework.id;

import com.studios.base.engine.framework.containers.util.EngineList;
import com.studios.base.engine.framework.debugging.DebugManager;

public class LineIDGen extends IDGen
{
	private EngineList<Integer> m_availableAlreadyUsedIDs;
	private boolean m_firstID = true;
	
	public LineIDGen()
	{
		m_availableAlreadyUsedIDs = new EngineList<Integer>();
	}

	@Override
	public int GetNextID() 
	{
		int Result = 0;
		
		if (m_firstID)
		{
			m_firstID = false;
			m_currentID = 1;
			return 0;
		}
		if (m_availableAlreadyUsedIDs.size() > 0)
		{
			int LowestID = 1230984554;
			for (Integer AvailableIndex : m_availableAlreadyUsedIDs)
			{
				if (LowestID == 1230984554)
					LowestID = AvailableIndex;
				else if (LowestID > AvailableIndex)
					LowestID = AvailableIndex;
				DebugManager.Log("LineIDGen", "Available IDs: " +  AvailableIndex);
			}
			Result = LowestID;
			DebugManager.Log("LineIDGen", "Lowest ID: " + LowestID);
		}
		
		if (Result == 0)
		{
			++m_currentID;
			return m_currentID - 1;
		}
		
		return Result;
	}
	
	public int GetCurrentID()
	{
		return GetCurrentID();
	}
	
	public boolean AddIDToRemovedList(int IDIndex)
	{
		if (m_availableAlreadyUsedIDs.contains(IDIndex))
			return false;
		return m_availableAlreadyUsedIDs.add(IDIndex);
	}
}
