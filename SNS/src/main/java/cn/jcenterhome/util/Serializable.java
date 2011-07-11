package cn.jcenterhome.util;

public interface Serializable {
	byte[] serialize();

	void unserialize(byte[] ss);
}