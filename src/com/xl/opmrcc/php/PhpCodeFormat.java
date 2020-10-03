package com.xl.opmrcc.php;

import java.util.ArrayList;

public class PhpCodeFormat {
	private PhpCodeParser mCCodeParser = null;
	int format_tabs=1;
	public PhpCodeFormat(PhpCodeParser codeParser) {
		mCCodeParser = codeParser;
	}
	
	
	public String getFormatedCode()
	{
		try {
			return getFormatedCodeEx();
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
		return "";
	}

	public String getFormatedCodeEx()
	{
		ArrayList<PhpCodeSpan> codeSpans = mCCodeParser.getCodeSpans();
		StringBuilder stringBuilder = new StringBuilder();
		int level = 0;
		boolean isIfWhileORFor = false;
		int inNumber = 0;
		boolean thisLineHasExtern = false;;
		
		for(int i=0;i<codeSpans.size();i++)
		{
			PhpCodeSpan codeSpan = codeSpans.get(i);
			PhpCodeSpan nextSpan = null;
			if(i+1<codeSpans.size())
				nextSpan = codeSpans.get(i+1);
			
			if(codeSpan.mContent.equals("case")||codeSpan.mContent.equals("default"))
			{
				if(stringBuilder.length()>0)
				{
					if(stringBuilder.charAt(stringBuilder.length()-1) == '\t')
					{
						stringBuilder.deleteCharAt(stringBuilder.length()-1);
					}
				}
			}
			
			if(codeSpan.mContent.equals("="))
			{
				stringBuilder.append(" ");
			}
			
			stringBuilder.append(codeSpan.mContent);

			if(codeSpan.mContent.equals("="))
			{
				stringBuilder.append(" ");
			}
			
			if(nextSpan!=null)
			{
				if( (
						codeSpan.mType == PhpCodeSpan.TYPE_KEY_WORDS 
						||codeSpan.mType == PhpCodeSpan.TYPE_WORDS
						||codeSpan.mType == PhpCodeSpan.TYPE_PRO_KEY_WORDS
						||codeSpan.mType == PhpCodeSpan.TYPE_CONSTANT
						||codeSpan.mType == PhpCodeSpan.TYPE_COMMENTS
						/*
						||(codeSpan.mType == CCodeSpan.TYPE_NONE&&
							(
								codeSpan.mContent.equals("&&")
								||codeSpan.mContent.equals("||")
								||codeSpan.mContent.equals("<<")
								||codeSpan.mContent.equals(">>")
								||codeSpan.mContent.equals("==")
								||codeSpan.mContent.equals(">=")
								||codeSpan.mContent.equals("<=")
								||codeSpan.mContent.equals("!=")
								||codeSpan.mContent.equals("+=")
								||codeSpan.mContent.equals("-=")
								||codeSpan.mContent.equals("*=")
								||codeSpan.mContent.equals("/=")
								||codeSpan.mContent.equals("&=")
								||codeSpan.mContent.equals("|=")
								||codeSpan.mContent.equals("<<=")
								||codeSpan.mContent.equals(">>=")
								||codeSpan.mContent.equals("^=")
							) )*/
					) && 
					(
						nextSpan.mType == PhpCodeSpan.TYPE_KEY_WORDS 
						||nextSpan.mType == PhpCodeSpan.TYPE_WORDS
						||nextSpan.mType == PhpCodeSpan.TYPE_PRO_KEY_WORDS
						||nextSpan.mType == PhpCodeSpan.TYPE_CONSTANT
						||nextSpan.mType == PhpCodeSpan.TYPE_COMMENTS
						/*
						||(nextSpan.mType == CCodeSpan.TYPE_NONE&&
							(
								nextSpan.mContent.equals("&&")
								||nextSpan.mContent.equals("||")
								||nextSpan.mContent.equals("<<")
								||nextSpan.mContent.equals(">>")
								||nextSpan.mContent.equals("==")
								||nextSpan.mContent.equals(">=")
								||nextSpan.mContent.equals("<=")
								||nextSpan.mContent.equals("!=")
								||nextSpan.mContent.equals("+=")
								||nextSpan.mContent.equals("-=")
								||nextSpan.mContent.equals("*=")
								||nextSpan.mContent.equals("/=")
								||nextSpan.mContent.equals("&=")
								||nextSpan.mContent.equals("|=")
								||nextSpan.mContent.equals("<<=")
								||nextSpan.mContent.equals(">>=")
								||nextSpan.mContent.equals("^=")
							) )*/
					) )
				{
					stringBuilder.append(" ");
				}
			}
			
			if(codeSpan.mContent.equals("{"))
			{
				if(thisLineHasExtern)
				{
					
				}
				else
				{
					level++;
					if( nextSpan!=null && (!nextSpan.mContent.equals("\n")) )
					{
						stringBuilder.append("\n");
						stringBuilder.append(makeSameChars(level*format_tabs,' '));
					}
				}
			}
			else
			if(codeSpan.mContent.equals("}"))
			{
				level--;
				if(level<0)
					level = 0;
			}
			else
			if(codeSpan.mContent.equals("if")||codeSpan.mContent.equals("while")||codeSpan.mContent.equals("for"))
			{
				isIfWhileORFor = true;
				inNumber = 0;
				level++;
			}
			else
			if(codeSpan.mContent.equals("else"))
			{
				if((nextSpan.mContent.equals("if")))
				{
					
				}
				else
				if((!nextSpan.mContent.equals("\n")))
				{
					if(!nextSpan.mContent.equals("{"))
					{
						stringBuilder.append("\n");
						stringBuilder.append(makeSameChars(level*(format_tabs+1),' '));
					}
				}
				else
				{
					stringBuilder.append("\n");
					stringBuilder.append(makeSameChars(level*format_tabs,' '));
					i++;
					if( (i+1<codeSpans.size())&&(!codeSpans.get(i+1).mContent.equals("{")) )
					{
						stringBuilder.append("\t");
					}
				}
			}
			else
			if(codeSpan.mContent.equals("extern"))
			{
				thisLineHasExtern = true;
			}
			else
			if(codeSpan.mContent.equals("("))
			{
				if(isIfWhileORFor)
				{
					inNumber++;
				}
				level++;
			}
			else
			if(codeSpan.mContent.equals(")"))
			{
				level--;
				if(level<0)
					level = 0;
				
				if(isIfWhileORFor)
				{
					inNumber--;
					if(inNumber<=0)
					{
						isIfWhileORFor = false;
						inNumber = 0;
						level--;
						
						PhpCodeSpan tempSpan = null;
						for(;i+1<codeSpans.size();i++)
						{
							tempSpan = codeSpans.get(i+1);
							if(!(tempSpan.mType == PhpCodeSpan.TYPE_COMMENTS))
								break;
							else
							{
								stringBuilder.append(tempSpan.mContent);
							}
						}
						
						if(tempSpan!=null )
						{
							if((!tempSpan.mContent.equals("\n")))
							{
								if(!tempSpan.mContent.equals("{"))
								{
									stringBuilder.append("\n");
									stringBuilder.append(makeSameChars(level*(format_tabs+1),' '));
								}
							}
							else
							{
								stringBuilder.append("\n");
								stringBuilder.append(makeSameChars(level*format_tabs,' '));
								i++;
								if( (i+1<codeSpans.size())&&(!codeSpans.get(i+1).mContent.equals("{")) )
								{
									stringBuilder.append("\t");
								}
							}
						}
					}
				}
				
			}else
			if(codeSpan.mContent.equals(";"))
			{
				if(isIfWhileORFor)
					;
				else
				{
					PhpCodeSpan tempSpan = null;
					for(;i+1<codeSpans.size();i++)
					{
						tempSpan = codeSpans.get(i+1);
						if(!(tempSpan.mType == PhpCodeSpan.TYPE_COMMENTS))
							break;
						else
						{
							stringBuilder.append(tempSpan.mContent);
						}
					}
					if( tempSpan!=null && (!tempSpan.mContent.equals("\n")) )
					{
						stringBuilder.append("\n");
						stringBuilder.append(makeSameChars(level*format_tabs,' '));
					}
				}
			}
			
			if(codeSpan.mContent.equals("\n"))
			{
				thisLineHasExtern = false;
				if( nextSpan!=null && (nextSpan.mContent.equals(")")) )
				{
					stringBuilder.append(makeSameChars(level*format_tabs,' '));
				}
				else
				if( nextSpan!=null && (nextSpan.mContent.equals("}")) )
				{
					stringBuilder.append(makeSameChars((level-1)*(format_tabs),' '));//xlformat
				}
				else
					stringBuilder.append(makeSameChars(level*format_tabs,' '));
			}
			else
			{
				//stringBuilder.append(" ");
				if( nextSpan!=null && (nextSpan.mContent.equals("}")) )
				{
					stringBuilder.append("\n");
					stringBuilder.append(makeSameChars((level-1)*(format_tabs),' ')); //xlformat
				}
			}
		}
		return stringBuilder.toString();
	}

	public void setTabs(int tabs){
		this.format_tabs = tabs;
	}
	
	private static String makeSameChars(int n,char ch)
	{
		StringBuffer str = new StringBuffer();
		for(;n>0;n--)
		{
			str.append(ch);
		}
		return str.toString();
	}
}
