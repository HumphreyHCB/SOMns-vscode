// CheckStyle: start generated
package com.oracle.truffle.sl.test;

import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.api.instrumentation.TruffleInstrument;
import com.oracle.truffle.api.instrumentation.TruffleInstrument.Provider;
import com.oracle.truffle.api.instrumentation.TruffleInstrument.Registration;
import com.oracle.truffle.sl.test.SLInstrumentLegacyTest.TestRedoIOLegacy;
import java.util.Arrays;
import java.util.Collection;

@GeneratedBy(TestRedoIOLegacy.class)
@Registration(id = "testRedoIOLegacy")
public final class SLInstrumentLegacyTestTestRedoIOLegacyProvider implements Provider {

    @Override
    public TruffleInstrument create() {
        return new TestRedoIOLegacy();
    }

    @Override
    public String getInstrumentClassName() {
        return "com.oracle.truffle.sl.test.SLInstrumentLegacyTest$TestRedoIOLegacy";
    }

    @Override
    public Collection<String> getServicesClassNames() {
        return Arrays.asList("com.oracle.truffle.sl.test.SLInstrumentLegacyTest$TestRedoIOLegacy");
    }

}