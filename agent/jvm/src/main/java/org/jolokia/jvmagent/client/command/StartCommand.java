package org.jolokia.jvmagent.client.command;

/*
 * Copyright 2009-2013 Roland Huss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.lang.reflect.InvocationTargetException;

import org.jolokia.jvmagent.client.util.OptionsAndArgs;
import org.jolokia.jvmagent.client.util.VirtualMachineHandler;

/**
 * Load a Jolokia Agent and start it. Whether an agent is started is decided by the existence of the
 * system property {@see JvmAgent#JOLOKIA_AGENT_URL}.
 *
 * @author roland
 * @since 06.10.11
 */
public class StartCommand extends AbstractBaseCommand {

    @Override
    /** {@inheritDoc} */
    String getName() {
        return "start";
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("PMD.SystemPrintln")
    int execute(OptionsAndArgs pOpts, Object pVm, VirtualMachineHandler pHandler)
        throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        String agentUrl;
        agentUrl = checkAgentUrl(pVm);
        boolean quiet = pOpts.isQuiet();
        if (agentUrl == null) {
            loadAgent(pVm, pOpts);
            agentUrl = checkAgentUrl(pVm);
            if (agentUrl == null) {
                // Wait a bit and try again
                agentUrl = checkAgentUrl(pVm, 500);
                if (agentUrl == null) {
                    System.err.println("Couldn't start agent for " + getProcessDescription(pOpts, pHandler));
                    System.err.println("Possible reason could be that port '" + pOpts.getPort() + "' is already occupied.");
                    System.err.println("Please check the standard output of the target process for a detailed error message.");
                    return 1;
                }
            }
            if (!quiet) {
                System.out.println("Started Jolokia for " + getProcessDescription(pOpts,pHandler));
                System.out.println(agentUrl);
            }
            return 0;
        } else {
            if (!quiet) {
                System.out.println("Jolokia is already attached to " + getProcessDescription(pOpts,pHandler));
                System.out.println(agentUrl);
            }
            return 1;
        }
    }

}
