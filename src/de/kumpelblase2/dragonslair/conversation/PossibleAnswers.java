package de.kumpelblase2.dragonslair.conversation;

import java.util.*;

public class PossibleAnswers
{
	public static final Set<String> approvalWords;
	public static final Set<String> disapprovalWords;
	public static final Set<String> considerationWords;
	static
	{
		final String[] approvals = new String[] { "yes", "sure", "yeah", "why not", "possibly", "ok", "k", "kk", "of cause", "no problem", "yep", "yip", "yea" };
		final String[] disapprovals = new String[] { "no", "never", "nope", "not", "pff", "i can't" };
		final String[] considering = new String[] { "well", "don't know", "not sure", "maybe", "i dont have", "probably", "sorry"/* yes I know it doesn't fit in here */};
		approvalWords = new HashSet<String>(Arrays.asList(approvals));
		disapprovalWords = new HashSet<String>(Arrays.asList(disapprovals));
		considerationWords = new HashSet<String>(Arrays.asList(considering));
	}
}
