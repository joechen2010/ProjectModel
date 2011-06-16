/**
 * Copyright (c) http://www.hao-se.cn Ltd.,2007 All  rights  reserved.
 */
package com.joe.utilities.common.util;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

/**
 * 用于hibernate中id的generator生成器
 * 
 *  
 */
public class Guid implements IdentifierGenerator {

	private String valueBeforeMD5 = "";

	private String valueAfterMD5 = "";

	private static Random myRand;

	private static SecureRandom mySecureRand;

	/*
	 * Static block to take care of one time secureRandom seed. It takes a few
	 * seconds to initialize SecureRandom. You might want to consider removing
	 * this static block or replacing it with a "time since first loaded" seed
	 * to reduce this time. This block will run only once per JVM instance.
	 */
	static {
		mySecureRand = new SecureRandom();
		long secureInitializer = mySecureRand.nextLong();

		myRand = new Random(secureInitializer);
	}

	/*
	 * Default constructor. With no specification of security option, this
	 * constructor defaults to lower security, high performance.
	 */

	/*
	 * Constructor with security option. Setting secure true enables each random
	 * number generated to be cryptographically strong. Secure false defaults to
	 * the standard Random function seeded with a single cryptographically
	 * strong random number.
	 */

	/*
	 * Method to generate the random GUID
	 */

	/*
	 * Convert to the standard format for GUID (Useful for SQL Server
	 * UniqueIdentifiers, etc.) Example: C2FEEEAC-CFCD-11D1-8B05-00600806D9B6
	 */
	public String toString() {
		String raw = valueAfterMD5.toUpperCase();
		StringBuffer sb = new StringBuffer();

		sb.append(raw.substring(0, 8));
		sb.append("-");
		sb.append(raw.substring(8, 12));
		sb.append("-");
		sb.append(raw.substring(12, 16));
		sb.append("-");
		sb.append(raw.substring(16, 20));
		sb.append("-");
		sb.append(raw.substring(20));

		return sb.toString();
	}

	public Serializable generate(SessionImplementor arg0, Object arg1) throws HibernateException {
		MessageDigest md5 = null;
		mySecureRand = new SecureRandom();
		long secureInitializer = mySecureRand.nextLong();

		myRand = new Random(secureInitializer);
		StringBuffer sbValueBeforeMD5 = new StringBuffer();
		StringBuffer sb = new StringBuffer();
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Error: " + e);
		}

		try {
			InetAddress id = InetAddress.getLocalHost();
			long time = System.currentTimeMillis();
			long rand = 0;
			rand = mySecureRand.nextLong();
			// rand = myRand.nextLong();

			// This StringBuffer can be a long as you need; the MD5
			// hash will always return 128 bits. You can change
			// the seed to include anything you want here.
			// You could even stream a file through the MD5 making
			// the odds of guessing it at least as great as that
			// of guessing the contents of the file!
			sbValueBeforeMD5.append(id.toString());
			sbValueBeforeMD5.append(":");
			sbValueBeforeMD5.append(Long.toString(time));
			sbValueBeforeMD5.append(":");
			sbValueBeforeMD5.append(Long.toString(rand));

			valueBeforeMD5 = sbValueBeforeMD5.toString();
			md5.update(valueBeforeMD5.getBytes());

			byte[] array = md5.digest();

			for (int j = 0; j < array.length; ++j) {
				int b = array[j] & 0xFF;

				if (b < 0x10)
					sb.append('0');
				sb.append(Integer.toHexString(b));
			}
		} catch (UnknownHostException e) {
			System.out.println("Error:" + e);
		}
		String raw = sb.toString().toUpperCase();
		StringBuffer sb1 = new StringBuffer();

		sb1.append(raw.substring(0, 8));
		sb1.append("-");
		sb1.append(raw.substring(8, 12));
		sb1.append("-");
		sb1.append(raw.substring(12, 16));
		sb1.append("-");
		sb1.append(raw.substring(16, 20));
		sb1.append("-");
		sb1.append(raw.substring(20));
		return sb1.toString();

	}

	public static String NewGuid() {
		MessageDigest md5 = null;
		mySecureRand = new SecureRandom();
		long secureInitializer = mySecureRand.nextLong();

		myRand = new Random(secureInitializer);
		StringBuffer sbValueBeforeMD5 = new StringBuffer();
		StringBuffer sb = new StringBuffer();
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Error: " + e);
		}

		try {
			InetAddress id = InetAddress.getLocalHost();
			long time = System.currentTimeMillis();
			long rand = 0;
			rand = mySecureRand.nextLong();
			// rand = myRand.nextLong();

			// This StringBuffer can be a long as you need; the MD5
			// hash will always return 128 bits. You can change
			// the seed to include anything you want here.
			// You could even stream a file through the MD5 making
			// the odds of guessing it at least as great as that
			// of guessing the contents of the file!
			sbValueBeforeMD5.append(id.toString());
			sbValueBeforeMD5.append(":");
			sbValueBeforeMD5.append(Long.toString(time));
			sbValueBeforeMD5.append(":");
			sbValueBeforeMD5.append(Long.toString(rand));

			String valueBeforeMD5new = sbValueBeforeMD5.toString();
			md5.update(valueBeforeMD5new.getBytes());

			byte[] array = md5.digest();

			for (int j = 0; j < array.length; ++j) {
				int b = array[j] & 0xFF;

				if (b < 0x10)
					sb.append('0');
				sb.append(Integer.toHexString(b));
			}
		} catch (UnknownHostException e) {
			System.out.println("Error:" + e);
		}
		String raw = sb.toString().toUpperCase();
		StringBuffer sb1 = new StringBuffer();

		sb1.append(raw.substring(0, 8));
		sb1.append("-");
		sb1.append(raw.substring(8, 12));
		sb1.append("-");
		sb1.append(raw.substring(12, 16));
		sb1.append("-");
		sb1.append(raw.substring(16, 20));
		sb1.append("-");
		sb1.append(raw.substring(20));
		return sb1.toString();
	}
}