/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.gui.controllers;

import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MessageBox;

import com.db4o.browser.gui.controllers.tree.TreeController;
import com.db4o.browser.gui.dialogs.IListPopulator;
import com.db4o.browser.gui.dialogs.ListSelector;
import com.db4o.browser.gui.views.DbBrowserPane;
import com.db4o.browser.model.*;
import com.db4o.browser.model.BrowserCore;
import com.db4o.browser.model.GraphPosition;
import com.db4o.browser.model.IGraphIterator;
import com.db4o.browser.model.nodes.ClassNode;
import com.db4o.reflect.ReflectClass;

/**
 * BrowserController.  The root MVC Controller for a browser window.
 *
 * @author djo
 */
public class BrowserController implements IBrowserController {
    
    protected DbBrowserPane ui;
    protected QueryController queryController;  // Used for running queries

    private Db4oConnectionSpec currentConnection;
    
	private TreeController treeController;
	private DetailController detailController;
	private SelectionChangedController selectionChangedController;
	private NavigationController navigationController;
	private PathLabelController pathController;
    private SearchController searchController;
    private IGraphIterator input;
    private GraphPosition initialSelection;

	/**
     * Constructor BrowserController.  Create a BrowserController for a
     * particular user interface.
	 * @param ui The DbBrowserPane to use as for the user interface
	 * @param queryController The QueryController used for opening queries
	 */
	public BrowserController(DbBrowserPane ui, final QueryController queryController) {
        this.ui = ui;
        this.queryController = queryController;

		// Initialize the ObjectTree's controllers
		selectionChangedController = new SelectionChangedController();
		
		treeController = new TreeController(this, ui.getObjectTree());
		detailController = new DetailController(this, ui);
		navigationController = new NavigationController(ui.getLeftButton(), ui.getRightButton());
        searchController = new SearchController(this, ui, navigationController);
		pathController = new PathLabelController(ui.getPathLabel());
        addQueryButtonHandler();
	}

	protected void addQueryButtonHandler() {
        ui.getQueryButton().addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                newQuery();
            }
        });
    }
    
    public void newQuery() {
        if (!BrowserCore.getDefault().isOpen()) {
            MessageBox error = new MessageBox(ui.getShell(), SWT.ICON_ERROR);
            error.setMessage("Please open a database before querying.");
            error.setText("Error");
            error.open();
            return;
        }
        ReflectClass toOpen = chooseClass();
        if (toOpen != null) {
            queryController.open(toOpen, currentConnection.path());
        }
    }

    /**
     * Method open.  Open a database file.
     * 
	 * @param file The platform-specific path/file name.
	 */
	public boolean open(String file) {
        return open(new Db4oFileConnectionSpec(file, Db4oConnectionSpec.PREFERENCE_IS_READ_ONLY)); 
	}
    
    public boolean open(String host, int port, String user, String password) {
        return open(new Db4oSocketConnectionSpec(host, port, user, password,Db4oConnectionSpec.PREFERENCE_IS_READ_ONLY));
    }
    
    private boolean open(Db4oConnectionSpec spec){
        currentConnection = spec;
        IGraphIterator i;
        try {
            i = BrowserCore.getDefault().iterator(currentConnection);
        } catch (Exception e) {
            MessageBox messageBox = new MessageBox(ui.getShell(), SWT.ICON_ERROR);
            messageBox.setText("Error");
            messageBox.setMessage(e.getClass().getName() + ": " + e.getMessage());
            messageBox.open();
            return false;
        }
        setInput(i, null);
        return true;
    }

    /**
     * Displays a class selection dialog box and allows the user to select a
     * class.
     * 
     * @return The class the user chose or null if the user did not choosse one.
     */
    public ReflectClass chooseClass() {
        final IGraphIterator iterator = BrowserCore.getDefault().iterator(currentConnection);
        ListSelector dialog = new ListSelector(ui.getShell());
        dialog.setText("Query a type");
        final HashMap choices = new HashMap();
        dialog.setListPopulator(new IListPopulator() {
            public void populate(List list) {
                int position=0;
                while (iterator.hasNext()) {
                    ClassNode node = (ClassNode) iterator.next();
                    ReflectClass clazz = node.getReflectClass();
                    list.add(clazz.getName());
                    choices.put(new Integer(position), clazz);
                    ++position;
                }
            }
        });
        dialog.open();
        if (dialog.getSelection() >= 0) {
            return (ReflectClass) choices.get(new Integer(dialog.getSelection()));
        } else {
            return null;
        }
    }
	
	/**
     * @return Returns the initialSelection.
     */
    public GraphPosition getInitialSelection() {
        return initialSelection;
    }
    

    /**
     * @return Returns the input.
     */
    public IGraphIterator getInput() {
        return input;
    }
    

    /* (non-Javadoc)
	 * @see com.db4o.browser.gui.controllers.IBrowserController#open(com.db4o.browser.model.IGraphIterator)
	 */
	public void setInput(IGraphIterator input, GraphPosition selection) {
        this.input = input;
        this.initialSelection = selection;
        
		// Set the various sub-controllers' inputs
		treeController.setInput(input, selection);
		detailController.setInput(input, selection);
		navigationController.setInput(input, selection);
		pathController.setInput(input, selection);
	}

	/**
	 * Returns the SelectionChangedController for this window.
	 * 
	 * @return SelectionChangedController the current SelectionChangedController
	 */
	public SelectionChangedController getSelectionChangedController() {
		return selectionChangedController;
	}
//
//	public void addToClasspath(File file) {
//		try {
//			Class urlclclass=Class.forName("java.net.URLClassLoader");
//			ClassLoader loader=getClass().getClassLoader();
//			while(loader!=null) {
//				if(urlclclass.isAssignableFrom(loader.getClass())) {
//					Method addmethod=urlclclass.getDeclaredMethod("addURL",new Class[]{URL.class});
//					addmethod.setAccessible(true);
//					addmethod.invoke(loader,new Object[]{file.toURL()});
//					return;
//				}
//				loader=loader.getParent();
//			}
//            Logger.log().error(new RuntimeException(), "Could not find a URLClassLoader.");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

    /**
     * @return Returns the queryController.
     */
    public QueryController getQueryController() {
        return queryController;
    }
    
    public Db4oConnectionSpec getCurrentConnection() {
        return currentConnection;
    }

    /**
     * Remove any selection from the tree
     */
    public void deselectAll() {
        treeController.deselectAll();
        detailController.deselectAll();
        navigationController.resetUndoRedoStack();
    }


    
}
