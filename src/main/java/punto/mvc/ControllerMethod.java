package punto.mvc;

import java.lang.reflect.Method;

/**
 * Created by MattUpstairs on 14/09/2014.
 */
public class ControllerMethod {
    Method method;
    Class<? extends Controller> controlClass;

    public ControllerMethod(Method method, Class<? extends Controller> controlClass) {
        this.method = method;
        this.controlClass = controlClass;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<? extends Controller> getControlClass() {
        return controlClass;
    }

    public void setControlClass(Class<? extends Controller> controlClass) {
        this.controlClass = controlClass;
    }
}
