package com.xl.opmrcc.php;

import java.util.LinkedList;

public class PhpCodeProDefine {
	public PhpCodeSpan mKey = null;
	public LinkedList<PhpCodeSpan> mCodeSpans = new LinkedList<PhpCodeSpan>();
	@Override
	public String toString() {
		return mCodeSpans.toString();
	}
}
