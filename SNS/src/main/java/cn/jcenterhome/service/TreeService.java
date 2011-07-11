package cn.jcenterhome.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jcenterhome.util.Common;

public class TreeService {

	private Map<Object, Map> data = new HashMap<Object, Map>();
	private Map<Object, List<Object>> child = new HashMap<Object, List<Object>>();
	private Map<Object, Integer> layer = new HashMap<Object, Integer>();
	private Map parent = new HashMap();
	private Object countId = 0;

	public TreeService() {
		child.put(-1, new ArrayList<Object>());
		layer.put(-1, -1);
	}

	public void setNode(int id, Object parent, Map value) {
		parent = !Common.empty(parent) ? parent : 0;
		data.put(id, value);
		List<Object> tempList = child.get(parent);
		if (tempList == null) {
			tempList = new ArrayList<Object>();
		}
		tempList.add(id);
		child.put(parent, tempList);
		this.parent.put(id, parent);
		if (layer.get(parent) == null) {
			layer.put(id, 0);
		} else {
			layer.put(id, layer.get(parent) + 1);
		}

	}

	public Map getValue(Object id) {
		return this.data.get(id);
	}

	public void getList(List<Object> tree, Object root) {
		List<Object> childList = this.child.get(root);
		if (childList != null) {
			for (Object id : childList) {
				tree.add(id);
				if (this.child.get(id) != null) {
					this.getList(tree, id);
				}
			}
		}
	}

	public List<Object> getChilds(Object id) {
		List<Object> child = new ArrayList<Object>();
		this.getList(child, id);
		return child;
	}

	public Integer getLayer(Object id, int space) {
		layer.put(id, 0);
		countId = id;
		reSetLayer(id);
		if (space > 0) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < this.layer.get(id); i++) {
				sb.append(space);
			}
			return Common.intval(sb.toString());
		} else {
			return this.layer.get(id);
		}
	}

	public void reSetLayer(Object id) {
		if (!Common.empty(parent.get(id))) {
			layer.put(countId, layer.get(countId) + 1);
			reSetLayer(parent.get(id));
		}
	}
}
