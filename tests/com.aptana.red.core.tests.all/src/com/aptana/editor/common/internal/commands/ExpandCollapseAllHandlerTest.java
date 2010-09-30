package com.aptana.editor.common.internal.commands;

import java.lang.reflect.Method;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import com.aptana.editor.common.outline.CommonOutlinePage;
import com.aptana.editor.common.tests.SingleEditorTestCase;
import com.aptana.editor.html.Activator;

public class ExpandCollapseAllHandlerTest extends SingleEditorTestCase
{

	private static final String EXPAND_ALL_COMMAND_ID = "com.aptana.editor.commands.ExpandAll";
	private static final String COLLAPSE_ALL_COMMAND_ID = "com.aptana.editor.commands.CollapseAll";
	private static final String PROJECT_NAME = "expand_collapse";

	@Override
	protected void setUp() throws Exception
	{
		Activator.getDefault();
		super.setUp();
	}

	@Override
	protected String getProjectName()
	{
		return PROJECT_NAME;
	}

	public void testExecute() throws Exception
	{
		// Create an open an HTML file
		createAndOpenFile("example" + System.currentTimeMillis() + ".html",
				"<html>\n<head>\n<title>Title goes here</title>\n</head>\n<body>\n<div>\n<p>Hi</p>\n</div>\n</body>");
		// Open the Outline view
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		IViewPart outline = page.showView(IPageLayout.ID_OUTLINE);

		// Grab tree viewer for outline contents
		IPage curPage = ((ContentOutline) outline).getCurrentPage();
		CommonOutlinePage outlinePage = (CommonOutlinePage) curPage;
		Method m = ContentOutlinePage.class.getDeclaredMethod("getTreeViewer");
		m.setAccessible(true);
		TreeViewer treeViewer = (TreeViewer) m.invoke(outlinePage);
//		Thread.sleep(500);

		// FIXME This assumes that the bundles are loaded and the folding is set up
//		// Grab the handler service to execute our command
//		IHandlerService service = (IHandlerService) outline.getSite().getService(IHandlerService.class);
//		service.executeCommand(EXPAND_ALL_COMMAND_ID, null);
//
//		// check expansion state, should be expanded
//		Object[] expanded = treeViewer.getExpandedElements();
//		assertEquals(4, expanded.length); // html, head, body, div
//
//		// toggle expansion
//		service.executeCommand(COLLAPSE_ALL_COMMAND_ID, null);
//
//		// check expansion state
//		expanded = treeViewer.getExpandedElements();
//		assertEquals(0, expanded.length); // collapsed
//
//		// toggle expansion
//		service.executeCommand(EXPAND_ALL_COMMAND_ID, null);
//
//		// check expansion state
//		expanded = treeViewer.getExpandedElements();
//		assertEquals(4, expanded.length); // html, head, body, div
	}

}
