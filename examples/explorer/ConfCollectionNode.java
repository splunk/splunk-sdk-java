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

import com.splunk.ConfCollection;
import com.splunk.Entity;
import com.splunk.EntityCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

// UNDONE: Some duplication of EntityConnectionNode below, eg: title count
// and size property - could probably be refactored into a shared 
// CollectionNode base class.
class ConfCollectionNode extends ExplorerNode {
    ConfCollectionNode(ConfCollection value) {
        super(value, new ConfCollectionKids(value));
        setDisplayName(String.format("Confs (%d)", value.size()));
    }

    @Override PropertyList getMetadata() {
         return new PropertyList() {{
            add(int.class, "size");
        }};
    }
}
