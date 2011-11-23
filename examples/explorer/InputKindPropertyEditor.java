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

import com.splunk.InputKind;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.util.Date;

public class InputKindPropertyEditor extends PropertyEditorSupport {
    @Override public String getAsText() {
        InputKind value = (InputKind)getValue();
        return value.toString();
    }
}
