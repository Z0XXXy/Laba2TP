import javax.json.bind.annotation.JsonbProperty;
import javax.validation.constraints.Min;

public class Msg{
  private String expression;

  public String getExpression() {
    return expression;
  }

  public void setExpression(String expression) {
    this.expression = expression;
  }
}
