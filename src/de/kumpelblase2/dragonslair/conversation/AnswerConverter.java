package de.kumpelblase2.dragonslair.conversation;

public class AnswerConverter
{
	private final String answer;
	
	public AnswerConverter(String answer)
	{
		this.answer = answer;
	}
	
	public AnswerType convert()
	{
		String temp = this.answer.toLowerCase();
		boolean approval = false;
		boolean disapproval = false;
		boolean considering = false;
		temp = temp.replaceAll("[^a-zA-Z 0-9]+"," ");
		if(!temp.startsWith(" "))
			temp = " " + temp;
		
		if(!temp.endsWith(" "))
			temp = temp + " ";
		
		for(String consider : PossibleAnswers.considerationWords)
		{
			if(temp.contains(" "+ consider + " "))
			{
				temp = temp.replace(" " + consider, "");
				considering = true;
			}
		}
		
		for(String correct : PossibleAnswers.approvalWords)
		{
			if(temp.contains(" " + correct + " "))
			{
				temp = temp.replace(" " + correct, "");
				approval = true;
			}
		}
		
		for(String incorrect : PossibleAnswers.disapprovalWords)
		{
			if(temp.contains(" "+ incorrect + " "))
			{
				temp = temp.replace(" " + incorrect, "");
				disapproval = true;
			}
		}
		
		if(approval && !disapproval && !considering)
			return AnswerType.AGREEMENT;
		else if(approval && !disapproval && considering)
			return AnswerType.CONSIDERING_AGREEMENT;
		else if(!approval && !disapproval && !considering)
			return AnswerType.NOTHING;
		else if(!approval && disapproval && !considering)
			return AnswerType.DISAGREEMENT;
		else if(!approval && disapproval && considering)
			return AnswerType.CONSIDERING_DISAGREEMENT;
		else if(!approval && !disapproval && considering)
			return AnswerType.CONSIDERING;
		else if(approval && disapproval && !considering)
			return AnswerType.CONSIDERING;
		
		return AnswerType.CONSIDERING;
	}
}
