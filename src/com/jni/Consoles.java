package com.jni;

public class Consoles {
	// 静态代码加载 C 语言动态库 consoles.dll
	static {
		System.loadLibrary("consoles");	
	}	
	
	// 以下是 getPressedKey() 方法的返回键值
	// 注：其它 ASCII 码表中的字符，直接用 ASCII 码表示。
	//     如：小写字母 a 可表示为 'a' 或 0x61
 	//         空格可表示为 0x20
	public static final int F1          = 0xf03b;
	public static final int F2          = 0xf03c;
	public static final int F3          = 0xf03d;
	public static final int F4          = 0xf03e;
	public static final int F5          = 0xf03f;
	public static final int F6          = 0xf040;
	public static final int F7          = 0xf041;
	public static final int F8          = 0xf042;
	public static final int F9          = 0xf043;
	public static final int F10         = 0xf044;
	public static final int F11         = 0xe085;
	public static final int F12         = 0xe086;

	public static final int UP          = 0xe048;   // 上箭头
	public static final int DOWN        = 0xe050;   // 下箭头
	public static final int LEFT        = 0xe04b;   // 左箭头
	public static final int RIGHT       = 0xe04d;   // 右箭头

	public static final int INSERT      = 0xe052;
	public static final int HOME        = 0xe047;
	public static final int PAGEUP      = 0xe049;
	public static final int DELETE      = 0xe053;
	public static final int END         = 0xe04f;
	public static final int PAGEDOWN    = 0xe051;

	public static final int NONE        = 0x0000;   // 无键按下

 	// 默认构造方法私有化，这样用户无法创建对象，必须使用类名来调用静态方法
	private Consoles(){}

	// 以下是用 C 语言写的动态库中的方法，称为 native 方法
	// 清屏
	public static native void clearScreen();
	
	// 设置控制台前景色和背景色
	/*
	设置默认的控制台前景和背景颜色。
	颜色属性由两个十六进制数字指定 -- backgroundColor为背景，foregroundColor则为
	前景。每个数字可以为以下任何十六进制值之一:

		0 = 黑色       8 = 灰色
		1 = 蓝色       9 = 淡蓝色
		2 = 绿色       A = 淡绿色
		3 = 浅绿色     B = 淡浅绿色
		4 = 红色       C = 淡红色
		5 = 紫色       D = 淡紫色
		6 = 黄色       E = 淡黄色
		7 = 白色       F = 亮白色
	
	*/
	public static native void setScreenColor(int backgroundColor, int foregroundColor);
	
	// 恢复控制台前景色和背景颜色
	public static native void resetScreenColor();

	// 设置文本颜色（包括前景和背景色）
	// 颜色及其值对应表见上面 setScreenColor 方法的说明
	public static native void setTextColor(int backgroundColor, int foregroundColor);
	
	// 隐藏光标
	public static native void hideCursor();
	 
	// 显示光标
	public static native void showCursor();
	
	// 定位光标
	public static native void gotoXY(int x, int y);
	
	// 获取光标位置，横坐标在 int 值的高 16 位，纵坐标在 int 值的低 16 位
	private static native int _getXY();
	
	// 判断是否有键盘按键按下，返回 1 表示有按键按下，0 表示无按键按下
	public static native int kbHit();
	
	// 获取键盘按键值
	public static native int getCh();
	
	// 以下是非 JNI 
	// 获取光标位置，位置保存在 Positon 对象中
	public static Position getXY() {
		int xy = _getXY();
		return new Position((xy >> 16) & 0xffff, xy & 0xffff);	
	}
	
	// 判断并获取按下的键盘按键值
	// 此方法包装了以上 kbHit() 方法和 getCh() 方法，
	// 建议使用此方法来判断是否有按键按下并获取按下的按键值
	// 此方法代码同时也示范了如何使用 kbHit() 方法和 getCh() 方法
	// 返回 0 表示没有键按下，否则返回键值
	// 返回键值的 8 ~ 15位：
	// 0x00 表示只有一个键值的键，
	// 0xf0 表示 F1 ~ F10 功能键，
	// 0xe0 表示其它有两个键值的键
	public static int getPressedKey() {
		int keyValue = 0x0;
		if (kbHit() == 1) {  // 有键按下
			keyValue = getCh();  // 获取键值
			if (keyValue == 0x0) {   // F1 ~ F10 功能键（有两个键值）
				keyValue = 0xf000 + getCh();
			} else if (keyValue == 0xE0) {   // 另一些有两个键值的键
				keyValue = (keyValue << 8) + getCh();
			}
		}
		return keyValue;
 	}

	// 静态内部类 ---- 位置类
	public static class Position {
		public int x;
		public int y;
		
		public Position(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}

/*
getCh() 方法获取的键值

*凡是有两个键值的，第一个不是真实的键值，第二个才是真实键值，程序应做相应判断
*以下各键没有测到键值(8个键)：
	Print Screen/SysRq	Scroll Lock	Pause/Break
	CapsLock		Shift		Ctrl
	Alt			Num Lock

键		键值
-----------------------------
(键盘第一排功能键)
Esc		0x1b

F1		0x0   0x3b
F2		0x0   0x3c
F3		0x0   0x3d
F4		0x0   0x3e

F5		0x0   0x3f
F6		0x0   0x40
F7		0x0   0x41
F8		0x0   0x42

F9		0x0   0x43
F10		0x0   0x44
F11		0xe0  0x85
F12		0xe0  0x86

(主键盘各键---按从上到下从左到右的顺序排列，并且上下档一起列出)
`		0x60
~		0x7e
1		0x31
!		0x21
2		0x32
@		0x40
3		0x33
#		0x23
4		0x34
$		0x24
5		0x35
%		0x25
6		0x36
^		0x5e
7		0x37
&		0x26
8		0x38
*		0x2a
9		0x39
(		0x28
0		0x30
)		0x29
-		0x2d
_		0x5f
=		0x3d
+		0x2b
(退格)		0x8
(tab)		0x9
q		0x71
Q		0x51
w		0x77
W		0x57
e		0x65
E		0x45
r		0x72
R		0x52
t		0x74
T		0x54
y		0x79
Y		0x59
u		0x75
U		0x55
i		0x69
I		0x49
o		0x6f
O		0x4f
p		0x70
P		0x50
[		0x5b
{		0x7b
]		0x5d
}		0x7d
(回车)		0xd
a		0x61
A		0x41
s		0x73
S		0x53
d		0x64
D		0x44
f		0x66
F		0x46
g		0x67
G		0x47
h		0x68
H		0x48
j		0x6a
J		0x4a
k		0x6b
K		0x4b
l		0x6c
L		0x4c
;		0x3b
:		0x3a
'		0x27
"		0x22
\		0x5c
|		0x7c
z		0x7a
Z		0x5a
x		0x78
X		0x58
c		0x63
C		0x43
v		0x76
V		0x56
b		0x62
B		0x42
n		0x6e
N		0x4e
m		0x6d
M		0x4d
,		0x2c
<		0x3c
.		0x2e
>		0x3e
/		0x2f
?		0x3f
space		0x20

(主键盘右边各功能键)
Insert		0xe0   0x52
Home		0xe0   0x47
PageUp		0xe0   0x49
Delete		0xe0   0x53
End		0xe0   0x4f
PageDown	0xe0   0x51

(箭头键)
(上箭头)	0xe0   0x48
(下箭头)	0xe0   0x50
(左箭头)	0xe0   0x4b
(右箭头)	0xe0   0x4d

副键盘的各键值对应主键盘及相应功能键的键值




*/