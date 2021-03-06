package com.tencent.inop;

import java.util.ArrayList;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

/**
 * @author VinceXie vince.xie@qq.com
 * @version 2014-5-23 下午12:12:29 State
 */
public class MethodInfo implements Opcodes{

	public boolean unOverride=false;

	/**
	 * 经过proguard处理的方法，因此可以合并。<init>已排除。
	 */
	public boolean canMer=false;
	
	/**
	 * 是否可进行类外合并
	 */
	public boolean canMerOutside=false;
	
	private ArrayList<String> params = new ArrayList<String>();

	private String returnString = null;

	/**
	 * 所在的ClassNode
	 */
	public MClassNode classNode;

	/**
	 * 包含的真正method
	 */
	public MethodNode methodNode;



	public MethodInfo(MethodNode methodNode, MClassNode classNode) {
		this.methodNode = methodNode;
		this.classNode = classNode;

		analyseParams();
	}

	/**
	 * 用作与MappingReader的数据作对比
	 * @return 例如com.example.abc.ac(int,int)
	 */
	public String getMapString()
	{
		//<init>之类不在mapping，所以已经忽略掉。
		String mapString=classNode.name.replace("/", ".")+methodNode.name+"(";
		for(String param:params)
		{
			String tmp=null;
			char ch = param.charAt(0);
			if (ch == 'L') {
				tmp=param.substring(1, param.length()-1).replace("/", ".");
			} else if (ch == 'Z') {
				tmp="boolean";
			} else if (ch == 'B') {
				tmp="byte";
			} else if (ch == 'C') {
				tmp="char";
			} else if (ch == 'S') {
				tmp="short";
			} else if (ch == 'I') {
				tmp="int";
			} else if (ch == 'J') {
				tmp="long";
			} else if (ch == 'F') {
				tmp="float";
			} else if (ch == 'D') {
				tmp="double";
			} else if (ch == '[') {
				if(param.charAt(param.length()-1)==';')
				{
					tmp=param.substring(2, param.length()-1).replace("/", ".")+"[]";
				}
				else {
					tmp=param.substring(1, param.length()).replace("/", ".")+"[]";
				}
			} 
			mapString=mapString+tmp+",";
		}
		if(mapString.charAt(mapString.length()-1)!='(')
		{
			mapString=mapString.substring(0, mapString.length()-1)+")";
		}
		else {
			mapString=mapString+")";
		}
		

		return mapString;
	}
	public void analyseParams() {
		String param = methodNode.desc;
		int pos;
		for (pos = 1; pos < param.length(); pos++) {
			char ch = param.charAt(pos);
			if (ch == 'L') {
				int j = pos + 1;
				while ((ch = param.charAt(j)) != ';') {
					j++;
				}
				params.add(param.substring(pos, j + 1));
				pos = j;
			} else if (ch == 'Z') {
				params.add("Z");
			} else if (ch == 'B') {
				params.add("B");
			} else if (ch == 'C') {
				params.add("C");
			} else if (ch == 'S') {
				params.add("S");
			} else if (ch == 'I') {
				params.add("I");
			} else if (ch == 'J') {
				params.add("J");
			} else if (ch == 'F') {
				params.add("F");
			} else if (ch == 'D') {
				params.add("D");
			} else if (ch == '[') {
				pos++;
				if ((ch = param.charAt(pos)) != 'L') {
					params.add("L" + ch);

				} else {
					int j = pos + 1;
					while ((ch = param.charAt(j)) != ';') {
						j++;
					}
					params.add("[" + param.substring(pos, j + 1));
					pos = j;
				}
			} else if (ch == ')') {
				break;
			}

		}
		pos++;

		char ch = param.charAt(pos);
		if (ch == 'L') {
			int j = pos + 1;
			while ((ch = param.charAt(j)) != ';') {
				j++;
			}

			returnString = param.substring(pos, j + 1);
		} else if (ch == 'Z') {
			returnString = "Z";
		} else if (ch == 'B') {
			returnString = "B";
		} else if (ch == 'C') {
			returnString = "C";
		} else if (ch == 'S') {
			returnString = "S";
		} else if (ch == 'I') {
			returnString = "I";
		} else if (ch == 'J') {
			returnString = "J";
		} else if (ch == 'F') {
			returnString = "F";
		} else if (ch == 'D') {
			returnString = "D";
		} else if (ch == '[') {
			pos++;
			if ((ch = param.charAt(pos)) != 'L') {
				returnString = "[" + ch;

			} else {
				int j = pos + 1;
				while ((ch = param.charAt(j)) != ';') {
					j++;
				}
				returnString = "[" + param.substring(pos, j + 1);

			}
		}
	}

	
	@Override
	public String toString() {
		return classNode.name+":"+methodNode.name;
	}

	public static boolean isPublic(MethodNode methodNode)
	{
		if((methodNode.access&ACC_PUBLIC)==ACC_PUBLIC)
		{
			return true;
		}
		return false;
	}
	
	public static boolean isStatic(MethodNode methodNode)
	{

		if((methodNode.access&ACC_STATIC)==ACC_STATIC)
		{
			return true;
		}
		return false;
	}
	
	public static boolean isPrivate(MethodNode methodNode)
	{
		if((methodNode.access&ACC_PRIVATE)==ACC_PRIVATE)
		{
			return true;
		}
		return false;
	}
	public static boolean isAbstract(MethodNode methodNode)
	{
		if((methodNode.access&ACC_ABSTRACT)==ACC_ABSTRACT)
		{
			return true;
		}
		return false;
	}

}
