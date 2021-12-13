public class Lip {
  private String expression;

  @Override
  public String toString() {
    return "Lip{" +
        "expression='" + expression + '\'' +
        ", result=" + result +
        '}';
  }

  public String getExpression() {
    return expression;
  }

  public void setExpression(String expression) {
    this.expression = expression;
  }

  private int result;

  public int getResult() {
    return result;
  }

  public void setResult(int result) {
    this.result = result;
  }
}
