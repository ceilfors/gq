package com.ceilfors.transform.gq

/**
 * @author ceilfors
 */
enum SingletonCodeFlowListenerManager implements CodeFlowListener {

    INSTANCE;

    private List<CodeFlowListener> codeFlowListeners = [
            new GqFile()
    ]

    @Override
    void methodStarted(MethodInfo methodInfo) {
        codeFlowListeners*.methodStarted(methodInfo)
    }

    @Override
    void methodEnded(Object result) {
        codeFlowListeners*.methodEnded(result)
    }

    @Override
    void methodEnded() {
        codeFlowListeners*.methodEnded()
    }
}
