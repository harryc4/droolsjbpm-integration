/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.simulation.impl;

import java.util.ArrayList;
import java.util.List;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;

import org.drools.command.BatchExecutionCommand;
import org.drools.command.runtime.BatchExecutionCommandImpl;
import org.drools.fluent.batch.FluentBatchExecution;
import org.drools.fluent.batch.imp.FluentBatchExecutionImpl;
import org.drools.io.impl.ByteArrayResource;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.StatelessKnowledgeSession;
import org.junit.Test;
import static org.junit.Assert.*;

public class BatchFluentTest {

    @Test
    public void testBatchSimple() {


        FluentBatchExecution f = new FluentBatchExecutionImpl();

        List list = new ArrayList();

        // @formatter:off          
        BatchExecutionCommand cmd = f.newBatchExecution().setGlobal("list", list)
                .insert(new Person("yoda", 150)).set("y")
                .insert(new Person("salaboy", 28)).set("x")
                .fireAllRules()
                .getBatchExecution();


        assertEquals(4, ((BatchExecutionCommandImpl) cmd).getCommands().size());

        ExecutionResults results = createStatelessSession().execute(cmd);

        assertEquals(2, results.getIdentifiers().size());
        

        // @formatter:on

    }

    private StatelessKnowledgeSession createStatelessSession() {
        String str = "package org.drools.simulation.test\n"
                + "import " + Person.class.getName() + "\n"
                + "global java.util.List list\n"
                + "rule updateAge no-loop when  $p : Person() then  modify( $p ) { setAge( $p.getAge() + 10 ) }; end\n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(new ByteArrayResource(str.getBytes()), ResourceType.DRL);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        return kbase.newStatelessKnowledgeSession();

    }
}
