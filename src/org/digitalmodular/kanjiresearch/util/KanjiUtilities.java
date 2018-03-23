package org.digitalmodular.kanjiresearch.util;

import java.lang.Character.UnicodeBlock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mark Jeronimus
 */
// Created 2018-03-23
public final class KanjiUtilities {
	private KanjiUtilities() { throw new AssertionError(); }

	public static List<TaggedKanjiList> transpose(Iterable<TaggedKanjiList> componentLists) {
		Map<Integer, TaggedKanjiList> kanjiLists = new HashMap<>(20000);

		for (TaggedKanjiList componentList : componentLists) {
			Integer component = componentList.getTag();
			int     n         = componentList.size();
			for (int i = 0; i < n; i++) {
				Integer kanji = componentList.get(i);

				TaggedKanjiList kanjiList = kanjiLists.get(kanji);
				if (kanjiList == null) {
					kanjiList = new TaggedKanjiList(kanji);
					kanjiLists.put(kanji, kanjiList);
				}

				kanjiList.add(component);
			}
		}

		return new ArrayList<>(kanjiLists.values());
	}

	@SuppressWarnings("ObjectEquality") // Comparing identity, not equality.
	public static boolean isKanji(int codePoint) {
		UnicodeBlock block = UnicodeBlock.of(codePoint);

		return block == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS ||
		       block == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A ||
		       block == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B ||
		       block == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C ||
		       block == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D ||
		       block == UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS ||
		       block == UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT;
	}

	@SuppressWarnings("ObjectEquality") // Comparing identity, not equality.
	public static boolean isKana(int codePoint) {
		UnicodeBlock block = UnicodeBlock.of(codePoint);
		if (block != UnicodeBlock.KATAKANA &&
		    block != UnicodeBlock.HIRAGANA &&
		    block != UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION) {
			return false;
		}

		int type = Character.getType(codePoint);

		// Filter out punctuation and other non-letters
		return type == Character.OTHER_LETTER ||
		       type == Character.MODIFIER_LETTER;
	}
}
