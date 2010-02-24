package com.aptana.editor.common.internal.peer;

import junit.framework.TestCase;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

public class PeerCharacterCloserTest extends TestCase
{

	private static final char[] DEFAULT_PAIRS = new char[] { '[', ']', '(', ')', '{', '}', '\'', '\'', '"', '"', '<',
			'>', '`', '`' };

	private PeerCharacterCloser closer;
	private ITextViewer viewer;
	private IDocument document;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		viewer = new TextViewer(Display.getDefault().getActiveShell(), SWT.NONE);
		closer = new PeerCharacterCloser(viewer, DEFAULT_PAIRS)
		{
			@Override
			protected String getScopeAtOffset(IDocument document, int offset) throws BadLocationException
			{
				return "source.js";
			}
		};
	}

	@Override
	protected void tearDown() throws Exception
	{
		viewer = null;
		closer = null;
		document = null;
		super.tearDown();
	}

	protected IDocument setDocument(String src)
	{
		document = new Document(src);
		viewer.setDocument(document);
		return document;
	}

	public void testDoesntDoubleEndingUnclosedPair()
	{
		setDocument("\" ");
		Event e = new Event();
		e.character = '"';
		e.start = 0;
		e.end = 0;
		e.keyCode = 39;
		e.doit = true;
		e.stateMask = 131072;
		e.widget = viewer.getTextWidget();
		VerifyEvent event = new VerifyEvent(e);
		closer.verifyKey(event);

		// This looks kind of wrong here because the doc should really be '"" ', but since we're hacking the event
		// mechanism we're not really sending a keystroke here.
		assertEquals("\" ", document.get());
	}

	public void testAutoClosePair()
	{
		setDocument(" ");
		Event e = new Event();
		e.character = '"';
		e.start = 0;
		e.end = 0;
		e.keyCode = 39;
		e.doit = true;
		e.stateMask = 131072;
		e.widget = viewer.getTextWidget();
		VerifyEvent event = new VerifyEvent(e);
		closer.verifyKey(event);

		assertFalse(event.doit);
		assertEquals("\"\" ", document.get());
	}

	public void testDontCloseWhenSimpleUnopenedPairCloseCharFollows()
	{
		setDocument(" ) ");
		Event e = new Event();
		e.character = '(';
		e.start = 0;
		e.end = 0;
		e.keyCode = 39;
		e.doit = true;
		e.stateMask = 131072;
		e.widget = viewer.getTextWidget();
		VerifyEvent event = new VerifyEvent(e);
		closer.verifyKey(event);

		assertTrue(event.doit); // don't interfere
	}

	public void testDontCloseWhenUnOpenedPairFollows()
	{
		setDocument(" ()) ");
		Event e = new Event();
		e.character = '(';
		e.start = 0;
		e.end = 0;
		e.keyCode = 39;
		e.doit = true;
		e.stateMask = 131072;
		e.widget = viewer.getTextWidget();
		VerifyEvent event = new VerifyEvent(e);
		closer.verifyKey(event);

		assertTrue(event.doit); // don't interfere
	}

	public void testWrapSelected()
	{
		setDocument("selected ");
		viewer.setSelectedRange(0, 8);
		Event e = new Event();
		e.character = '"';
		e.start = 0;
		e.end = 0;
		e.keyCode = 39;
		e.doit = true;
		e.stateMask = 131072;
		e.widget = viewer.getTextWidget();
		VerifyEvent event = new VerifyEvent(e);
		closer.verifyKey(event);

		assertFalse(event.doit);
		assertEquals("\"selected\" ", document.get());
	}

	public void testUnpairedClose() throws Exception
	{
		char[] pairs = new char[] { '(', ')', '"', '"' };
		closer = new PeerCharacterCloser(null, pairs);
		StringBuilder builder = new StringBuilder();
		int times = 5000;
		for (int i = 0; i < times; i++)
		{
			builder.append("(");
		}
		for (int i = 0; i < times; i++)
		{
			builder.append(")");
		}
		assertFalse(closer.unpairedClose('(', ')', new Document(builder.toString()), times));
		builder.append(")");
		assertTrue(closer.unpairedClose('(', ')', new Document(builder.toString()), times));
	}

	public void testDontCloseWhenScopeIsComment()
	{
		setDocument(" ");
		closer = new PeerCharacterCloser(viewer, DEFAULT_PAIRS)
		{
			@Override
			protected String getScopeAtOffset(IDocument document, int offset) throws BadLocationException
			{
				return "source.js comment.block";
			}
		};
		Event e = new Event();
		e.character = '(';
		e.start = 0;
		e.end = 0;
		e.keyCode = 39;
		e.doit = true;
		e.stateMask = 131072;
		e.widget = viewer.getTextWidget();
		VerifyEvent event = new VerifyEvent(e);
		closer.verifyKey(event);

		assertTrue(event.doit); // don't interfere
	}
}
