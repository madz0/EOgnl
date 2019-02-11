package eognl.test.binding;

import org.junit.Test;

import eognl.EOgnl;
import eognl.EOgnlContext;
import eognl.EOgnlRuntime;
import eognl.OgnlContext;
import eognl.OgnlException;

import static org.junit.Assert.*;

import java.util.List;

public class ExpressionTest {
  
public static class MyTest {
    
    private List<Object> objects;

    public List<Object> getObjects() {
      return objects;
    }

    public void setObjects(List<Object> objects) {
      this.objects = objects;
    }
    
  }

  @Test
  public void testBinding() throws OgnlException, InstantiationException, IllegalAccessException {
    Object expression = EOgnl.parseExpression("name=test");
    EOgnlContext context = new EOgnlContext(OgnlContext.CONF_AUTO_EXPAND | OgnlContext.CONF_INIT_NULLS
        | OgnlContext.CONF_INIT_UNKNOWN | OgnlContext.CONF_UNKNOWN_TO_LITERAL | OgnlContext.CONF_CAST_PRIMITIVES);
    BindingModel obj = (BindingModel)EOgnlRuntime.createProperObject(BindingModel.class, BindingModel.class.getComponentType());
    EOgnl.getValue(expression, context, obj);
    assertEquals("test", obj.getName());
  }
  
  @Test
  public void testBinding2() throws OgnlException, InstantiationException, IllegalAccessException {
    Object expression = EOgnl.parseExpression("objects[0]=salam");
    EOgnlContext context = new EOgnlContext(OgnlContext.CONF_AUTO_EXPAND | OgnlContext.CONF_INIT_NULLS
        | OgnlContext.CONF_INIT_UNKNOWN | OgnlContext.CONF_UNKNOWN_TO_LITERAL | OgnlContext.CONF_CAST_PRIMITIVES);
    MyTest root = new MyTest();
    EOgnl.getValue(expression, context, root);
    assertEquals("salam", ((List)root.getObjects()).get(0));
  }
}
