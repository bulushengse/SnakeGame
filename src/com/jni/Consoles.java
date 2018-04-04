package com.jni;

public class Consoles {
	// ��̬������� C ���Զ�̬�� consoles.dll
	static {
		System.loadLibrary("consoles");	
	}	
	
	// ������ getPressedKey() �����ķ��ؼ�ֵ
	// ע������ ASCII ����е��ַ���ֱ���� ASCII ���ʾ��
	//     �磺Сд��ĸ a �ɱ�ʾΪ 'a' �� 0x61
 	//         �ո�ɱ�ʾΪ 0x20
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

	public static final int UP          = 0xe048;   // �ϼ�ͷ
	public static final int DOWN        = 0xe050;   // �¼�ͷ
	public static final int LEFT        = 0xe04b;   // ���ͷ
	public static final int RIGHT       = 0xe04d;   // �Ҽ�ͷ

	public static final int INSERT      = 0xe052;
	public static final int HOME        = 0xe047;
	public static final int PAGEUP      = 0xe049;
	public static final int DELETE      = 0xe053;
	public static final int END         = 0xe04f;
	public static final int PAGEDOWN    = 0xe051;

	public static final int NONE        = 0x0000;   // �޼�����

 	// Ĭ�Ϲ��췽��˽�л��������û��޷��������󣬱���ʹ�����������þ�̬����
	private Consoles(){}

	// �������� C ����д�Ķ�̬���еķ�������Ϊ native ����
	// ����
	public static native void clearScreen();
	
	// ���ÿ���̨ǰ��ɫ�ͱ���ɫ
	/*
	����Ĭ�ϵĿ���̨ǰ���ͱ�����ɫ��
	��ɫ����������ʮ����������ָ�� -- backgroundColorΪ������foregroundColor��Ϊ
	ǰ����ÿ�����ֿ���Ϊ�����κ�ʮ������ֵ֮һ:

		0 = ��ɫ       8 = ��ɫ
		1 = ��ɫ       9 = ����ɫ
		2 = ��ɫ       A = ����ɫ
		3 = ǳ��ɫ     B = ��ǳ��ɫ
		4 = ��ɫ       C = ����ɫ
		5 = ��ɫ       D = ����ɫ
		6 = ��ɫ       E = ����ɫ
		7 = ��ɫ       F = ����ɫ
	
	*/
	public static native void setScreenColor(int backgroundColor, int foregroundColor);
	
	// �ָ�����̨ǰ��ɫ�ͱ�����ɫ
	public static native void resetScreenColor();

	// �����ı���ɫ������ǰ���ͱ���ɫ��
	// ��ɫ����ֵ��Ӧ������� setScreenColor ������˵��
	public static native void setTextColor(int backgroundColor, int foregroundColor);
	
	// ���ع��
	public static native void hideCursor();
	 
	// ��ʾ���
	public static native void showCursor();
	
	// ��λ���
	public static native void gotoXY(int x, int y);
	
	// ��ȡ���λ�ã��������� int ֵ�ĸ� 16 λ���������� int ֵ�ĵ� 16 λ
	private static native int _getXY();
	
	// �ж��Ƿ��м��̰������£����� 1 ��ʾ�а������£�0 ��ʾ�ް�������
	public static native int kbHit();
	
	// ��ȡ���̰���ֵ
	public static native int getCh();
	
	// �����Ƿ� JNI 
	// ��ȡ���λ�ã�λ�ñ����� Positon ������
	public static Position getXY() {
		int xy = _getXY();
		return new Position((xy >> 16) & 0xffff, xy & 0xffff);	
	}
	
	// �жϲ���ȡ���µļ��̰���ֵ
	// �˷�����װ������ kbHit() ������ getCh() ������
	// ����ʹ�ô˷������ж��Ƿ��а������²���ȡ���µİ���ֵ
	// �˷�������ͬʱҲʾ�������ʹ�� kbHit() ������ getCh() ����
	// ���� 0 ��ʾû�м����£����򷵻ؼ�ֵ
	// ���ؼ�ֵ�� 8 ~ 15λ��
	// 0x00 ��ʾֻ��һ����ֵ�ļ���
	// 0xf0 ��ʾ F1 ~ F10 ���ܼ���
	// 0xe0 ��ʾ������������ֵ�ļ�
	public static int getPressedKey() {
		int keyValue = 0x0;
		if (kbHit() == 1) {  // �м�����
			keyValue = getCh();  // ��ȡ��ֵ
			if (keyValue == 0x0) {   // F1 ~ F10 ���ܼ�����������ֵ��
				keyValue = 0xf000 + getCh();
			} else if (keyValue == 0xE0) {   // ��һЩ��������ֵ�ļ�
				keyValue = (keyValue << 8) + getCh();
			}
		}
		return keyValue;
 	}

	// ��̬�ڲ��� ---- λ����
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
getCh() ������ȡ�ļ�ֵ

*������������ֵ�ģ���һ��������ʵ�ļ�ֵ���ڶ���������ʵ��ֵ������Ӧ����Ӧ�ж�
*���¸���û�в⵽��ֵ(8����)��
	Print Screen/SysRq	Scroll Lock	Pause/Break
	CapsLock		Shift		Ctrl
	Alt			Num Lock

��		��ֵ
-----------------------------
(���̵�һ�Ź��ܼ�)
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

(�����̸���---�����ϵ��´����ҵ�˳�����У��������µ�һ���г�)
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
(�˸�)		0x8
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
(�س�)		0xd
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

(�������ұ߸����ܼ�)
Insert		0xe0   0x52
Home		0xe0   0x47
PageUp		0xe0   0x49
Delete		0xe0   0x53
End		0xe0   0x4f
PageDown	0xe0   0x51

(��ͷ��)
(�ϼ�ͷ)	0xe0   0x48
(�¼�ͷ)	0xe0   0x50
(���ͷ)	0xe0   0x4b
(�Ҽ�ͷ)	0xe0   0x4d

�����̵ĸ���ֵ��Ӧ�����̼���Ӧ���ܼ��ļ�ֵ




*/