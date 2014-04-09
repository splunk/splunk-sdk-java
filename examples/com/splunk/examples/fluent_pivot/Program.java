/*
 * Copyright 2014 Splunk, Inc.
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
package com.splunk.examples.fluent_pivot;

import com.splunk.*;

public class Program {
    public static void main(String[] argv) {
        try {
            run(argv);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    static void run(String[] argsIn) throws Exception {
        Command command;
        Service service;

        command = Command.splunk("input");
        service = Service.connect(command.opts);

        DataModel dataModel = service.getDataModels().get("internal_audit_logs");
        DataModelObject searches = dataModel.getObject("searches");

        System.out.print("Working with object " + searches.getDisplayName());
        System.out.println(" in model " + dataModel.getDisplayName());
        System.out.print("  Lineage: ");
        for (String name : searches.getLineage()) {
            System.out.print(" -> " + name);
        }
        System.out.println();
        System.out.println("  Internal name: " + searches.getName());

        Job firstFiveEntries = searches.runQuery("| head 5");
        while (!firstFiveEntries.isDone()) {
            Thread.sleep(100);
        }

        ResultsReaderXml results = new ResultsReaderXml(firstFiveEntries.getResults());
        for (Event event : results) {
            System.out.println(event.toString());
        }

        System.out.println("~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Pivoting on searches");

        Pivot pivot = searches.createPivotSpecification().
                addRowSplit("user", "Executing user").
                addColumnSplit("exec_time", null, null, null, 4).
                addCellValue("search", "Search Query", StatsFunction.DISTINCT_VALUES, false).
                pivot();

        System.out.println("Query for binning search queries by execution time and executing user:");
        System.out.println("  " + pivot.getPrettyQuery());

        Job pivotJob = pivot.run();
        while (!pivotJob.isDone()) {
            Thread.sleep(100);
        }

        results = new ResultsReaderXml(pivotJob.getResults());
        for (Event event : results) {
            System.out.println(event.toString());
        }
    }
}


