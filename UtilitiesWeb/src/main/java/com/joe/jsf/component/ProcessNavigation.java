package com.joe.jsf.component;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.event.ActionEvent;

public class ProcessNavigation {
	private Map<String, Block> blockMap = new TreeMap<String, Block>();
	private ArrayList<Block> blockList = new ArrayList<Block>();
	private Block currentBlock;

	public Map<String, Block> getBlockMap() {
		return blockMap;
	}

	public void addBlock(String id, Block b) {
		blockMap.put(id, b);
		blockList.add(b);

		b.setInstanceId(id);
		b.setProcess(this);
		b.init();
	}

	public Block getBlock(String name) {
		return blockMap.get(name);
	}

	public ArrayList<Block> getBlocks() {
		return blockList;
	}

	public Block getCurrentBlock() {
		if (currentBlock == null) {
			currentBlock = getBlocks().get(0);
		}
		return currentBlock;
	}

	public void setCurrentBlock(Block currentBlock) {
		if (this.currentBlock != null)
			this.currentBlock.setBlockVisible(false);
		this.currentBlock = currentBlock;
		this.currentBlock.setBlockVisible(true);
	}

	public void setCurrentBlock(String blockId) {
		setCurrentBlock(blockMap.get(blockId));
	}

	public void next(ActionEvent event) {
		ArrayList<Block> blocks = getBlocks();
		setCurrentBlock(blocks.get(getCurrentBlockIndex() + 1));
	}

	private int getCurrentBlockIndex() {
		ArrayList<Block> blocks = getBlocks();
		for (int i = 0; i < blocks.size(); i++) {
			if (blocks.get(i) == currentBlock) {
				return i;
			}
		}
		return -1;
	}

	public void previous(ActionEvent event) {
		ArrayList<Block> blocks = getBlocks();
		setCurrentBlock(blocks.get(getCurrentBlockIndex() - 1));
	}

	public boolean isCurrentLast() {
		return getCurrentBlockIndex() == blockMap.size() - 1;
	}

	public boolean isCurrentFirst() {
		return getCurrentBlockIndex() == 0;
	}
		
	/**
	 * Placeholder for sub classes to implement (but not required so it's not abstract).
	 * 
	 * @param o
	 * @param flag
	 */
	public boolean mapObjects(Object o, String flag) {
		return true;
	}
	
}
