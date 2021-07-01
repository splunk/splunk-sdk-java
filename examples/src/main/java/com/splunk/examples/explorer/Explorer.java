/*
 * Copyright 2011 Splunk, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"): you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

//
// The NetBeans tutorial on which this sample is based:
//
//   http://blogs.oracle.com/geertjan/entry/netbeans_apis_outside_of_the
//

package com.splunk.examples.explorer;

import com.splunk.InputKind;
import com.splunk.Service;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.beans.PropertyEditorManager;
import java.util.Date;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.propertysheet.PropertySheetView;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

public class Explorer extends JFrame implements ExplorerManager.Provider { 
    private ExplorerManager manager;
    private Children.Array roots;

    static {
        // Register property editors for types that dont have a default editor.
        PropertyEditorManager.registerEditor(
            Date.class, DatePropertyEditor.class);
        PropertyEditorManager.registerEditor(
            InputKind.class, InputKindPropertyEditor.class);
        PropertyEditorManager.registerEditor(
            Map.class, MapPropertyEditor.class);
        PropertyEditorManager.registerEditor(
            String[].class, StringArrayPropertyEditor.class);
    }

    Explorer(Service service) {
        this.roots = new Children.Array();
        roots.add(new Node[] { new ServiceNode(service) });
        Node root = new AbstractNode(roots);
        root.setDisplayName("Root"); // Not visible
        this.manager = new ExplorerManager();
        this.manager.setRootContext(root);
        initialize();
    }

    public static Explorer create(Service service) {
        return new Explorer(service);
    }

    public ExplorerManager getExplorerManager() {
        return this.manager;
    }

    void initialize() {
        BeanTreeView left;
        left = new BeanTreeView();
        left.setRootVisible(false);

        PropertySheetView right;
        right = new PropertySheetView();
        right.setDescriptionAreaVisible(false);
        
        JSplitPane splitPane;
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        splitPane.setResizeWeight(0.4);
        splitPane.setDividerSize(3);

        setTitle("Splunk Explorer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().add(splitPane);

        // Place the window in a convenient position
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width / 2;
        int height = screenSize.height / 2;
        setSize(width, height);
        setLocation(width / 2, height / 2);
    }
}
