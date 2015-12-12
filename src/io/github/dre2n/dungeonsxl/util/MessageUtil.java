package io.github.dre2n.dungeonsxl.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class MessageUtil {
	
	public enum DefaultFontInfo {
		
		A('A', 5), a('a', 5), B('B', 5), b('b', 5), C('C', 5), c('c', 5), D('D', 5), d('d', 5), E('E', 5), e('e', 5), F('F', 5), f('f', 4), G('G', 5), g('g', 5), H('H', 5), h('h', 5), I('I', 3), i(
		        'i', 1), J('J', 5), j('j', 5), K('K', 5), k('k', 4), L('L', 5), l('l', 1), M('M', 5), m('m', 5), N('N', 5), n('n', 5), O('O', 5), o('o', 5), P('P', 5), p('p', 5), Q('Q', 5), q('q', 5), R(
		        'R', 5), r('r', 5), S('S', 5), s('s', 5), T('T', 5), t('t', 4), U('U', 5), u('u', 5), V('V', 5), v('v', 5), W('W', 5), w('w', 5), X('X', 5), x('x', 5), Y('Y', 5), y('y', 5), Z('Z', 5), z(
		        'z', 5), NUM_1('1', 5), NUM_2('2', 5), NUM_3('3', 5), NUM_4('4', 5), NUM_5('5', 5), NUM_6('6', 5), NUM_7('7', 5), NUM_8('8', 5), NUM_9('9', 5), NUM_0('0', 5), EXCLAMATION_POINT('!', 1), AT_SYMBOL(
		        '@', 6), NUM_SIGN('#', 5), DOLLAR_SIGN('$', 5), PERCENT('%', 5), UP_ARROW('^', 5), AMPERSAND('&', 5), ASTERISK('*', 5), LEFT_PARENTHESIS('(', 4), RIGHT_PERENTHESIS(')', 4), MINUS('-',
		        5), UNDERSCORE('_', 5), PLUS_SIGN('+', 5), EQUALS_SIGN('=', 5), LEFT_CURL_BRACE('{', 4), RIGHT_CURL_BRACE('}', 4), LEFT_BRACKET('[', 3), RIGHT_BRACKET(']', 3), COLON(':', 1), SEMI_COLON(
		        ';', 1), DOUBLE_QUOTE('"', 3), SINGLE_QUOTE('\'', 1), LEFT_ARROW('<', 4), RIGHT_ARROW('>', 4), QUESTION_MARK('?', 5), SLASH('/', 5), BACK_SLASH('\\', 5), LINE('|', 1), TILDE('~', 5), TICK(
		        '`', 2), PERIOD('.', 1), COMMA(',', 1), SPACE(' ', 3), DEFAULT('a', 4);
		
		private char character;
		private int length;
		
		DefaultFontInfo(char character, int length) {
			this.character = character;
			this.length = length;
		}
		
		public char getCharacter() {
			return character;
		}
		
		public int getLength() {
			return length;
		}
		
		public int getBoldLength() {
			if (this == DefaultFontInfo.SPACE) {
				return getLength();
			}
			return length + 1;
		}
		
		public static DefaultFontInfo getDefaultFontInfo(char c) {
			for (DefaultFontInfo dFI : DefaultFontInfo.values()) {
				if (dFI.getCharacter() == c) {
					return dFI;
				}
			}
			return DefaultFontInfo.DEFAULT;
		}
	}
	
	private final static int CENTER_PX = 154;
	
	public static void sendCenteredMessage(CommandSender sender, String message) {
		if (message == null || message.equals("")) {
			sender.sendMessage("");
		}
		
        message = ChatColor.translateAlternateColorCodes('&', message);
		
		int messagePxSize = 0;
		boolean previousCode = false;
		boolean isBold = false;
		
		for (char c : message.toCharArray()) {
			if (c == '§') {
				previousCode = true;
				continue;
			} else if (previousCode == true) {
				previousCode = false;
				if (c == 'l' || c == 'L') {
					isBold = true;
					continue;
				} else {
					isBold = false;
				}
			} else {
				DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
				messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
				messagePxSize++;
			}
		}
		
		int halvedMessageSize = messagePxSize / 2;
		int toCompensate = CENTER_PX - halvedMessageSize;
		int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
		int compensated = 0;
		StringBuilder sb = new StringBuilder();
		while (compensated < toCompensate) {
			sb.append(" ");
			compensated += spaceLength;
		}
		sender.sendMessage(sb.toString() + ChatColor.translateAlternateColorCodes('&', message));
	}
	
	public static void sendMessage(CommandSender sender, String message) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}
	
	public static void sendPluginTag(CommandSender sender, Plugin plugin) {
		sendCenteredMessage(sender, "&4&l[ &6" + plugin.getDescription().getName() + " &4&l]");
	}
	
	public static final String[] BIG_A =  {
		"IIIIIIIIIIII  ",
		" IIII   IIII  ",
		" IIIIIIIIIII  ",
		" IIII   IIII  ",
		"IIIIII IIIIII "
	};
	
	public static final String[] BIG_B =  {
		"IIIIIIIIIII  ",
		" IIII   IIII ",
		" IIIIIIIIIII ",
		" IIII   IIII ",
		"IIIIIIIIIII  "
	};
	
	public static final String[] BIG_C =  {
		"  IIIIIIIII  ",
		" IIII   IIII ",
		" III           ",
		" IIII   IIII ",
		"  IIIIIIIII  "
	};
	
	public static final String[] BIG_D =  {
		"IIIIIIIIIII  ",
		" IIII   IIII ",
		" IIII   IIII ",
		" IIII   IIII ",
		"IIIIIIIIIII  "
	};
	
	public static final String[] BIG_E =  {
		"IIIIIIIII ",
		" IIII   I ",
		" IIIIII   ",
		" IIII   I ",
		"IIIIIIIII "
	};
	
	public static final String[] BIG_F =  {
		" IIIIIIII ",
		" IIII     ",
		" IIIIII   ",
		" IIII     ",
		" IIII     "
	};
	
	public static final String[] BIG_G =  {
		"  IIIIIIII  ",
		" IIII       ",
		" III  IIIII ",
		" IIII   III ",
		"  IIIIIII I  "
	};
	
	public static final String[] BIG_H =  {
		"IIIIII IIIIII ",
		" IIII   IIII  ",
		" IIIIIIIIIII  ",
		" IIII   IIII  ",
		"IIIIII IIIIII "
	};
	
	public static final String[] BIG_I =  {
		"IIIIII ",
		" IIII  ",
		" IIII  ",
		" IIII  ",
		"IIIIII "
	};
	
	public static final String[] BIG_J =  {
		"   IIIIII ",
		"    IIII  ",
		"    IIII  ",
		"I  IIIII  ",
		" IIIIII   "
	};
	
	public static final String[] BIG_K =  {
		"IIIIII IIIII ",
		" IIII IIII   ",
		" IIIIIIII    ",
		" IIII IIII   ",
		"IIIIII IIIII "
	};
	
	public static final String[] BIG_L = {
		"IIII      ",
		"IIII      ",
		"IIII      ",
		"IIIIIIIII ",
		"IIIIIIIII "
	};
	
	public static final String[] BIG_M =  {
		"IIIIII    IIIIII ",
		" IIIIIIIIIIIIII  ",
		" IIII IIII IIII  ",
		" IIII  II  IIII  ",
		"IIIIII    IIIIII "
	};
	
	public static final String[] BIG_N =  {
		"IIIIIIII IIIIII ",
		" IIIIIIII IIII  ",
		" IIII IIIIIIII  ",
		" IIII  IIIIIII  ",
		"IIIIII  IIIIIII "
	};
	
	public static final String[] BIG_O =  {
		" IIIIIIII ",
		"IIII  IIII ",
		"III    III ",
		"IIII  IIII ",
		" IIIIIIII  "
	};
	
	public static final String[] BIG_P =  {
		"IIIIIIIII  ",
		"IIII  IIII ",
		"IIIIIIIII  ",
		"IIII       ",
		"IIII       "
	};
	
	public static final String[] BIG_Q =  {
		" IIIIIIII ",
		"IIII  IIII ",
		"III  I III ",
		"IIII  IIII ",
		" IIIIIIII I "
	};
	
	public static final String[] BIG_R =  {
		"IIIIIIIII  ",
		"IIII  IIII ",
		"IIIIIIIII  ",
		"IIII IIII  ",
		"IIII  IIII "
	};
	
	public static final String[] BIG_S =  {
		" IIIIII ",
		"IIII  I ",
		" IIIII  ",
		"I  IIII ",
		"IIIIII  "
	};
	
	public static final String[] BIG_T =  {
		"IIIIIIIIII ",
		"I  IIII  I ",
		"   IIII    ",
		"   IIII    ",
		"   IIII    "
	};
	
	public static final String[] BIG_U =  {
		"IIIII IIIII ",
		" IIII  IIII ",
		" IIII  IIII ",
		" IIII  IIII ",
		"  IIIIIIII  "
	};
	
	public static final String[] BIG_V =  {
		"IIIII IIIII ",
		" IIII  IIII ",
		" IIII  IIII ",
		"   IIIIII   ",
		"    IIII    "
	};
	
	public static final String[] BIG_W =  {
		"IIIII IIIII IIIII ",
		" IIII  IIII  IIII ",
		" IIII  IIII  IIII ",
		"   IIIIIIIIIIII   ",
		"    IIII  IIII    "
	};
	
	public static final String[] BIG_X = {
		"IIIII   IIIII ",
		"  IIII IIII   ",
		"   IIIIIII    ",
		"  IIII IIII   ",
		"IIIII   IIIII "
	};
	
	public static final String[] BIG_Y = {
		"IIIII    IIIII ",
		"  IIII  IIII   ",
		"   IIIIIIII    ",
		"     IIII      ",
		"     IIII      ",
		"    IIIIII     "
	};
	
	public static final String[] BIG_Z =  {
		"IIIIIIIIII ",
		"I    IIII  ",
		"   IIII    ",
		" IIII    I ",
		"IIIIIIIIII "
	};
	
}
