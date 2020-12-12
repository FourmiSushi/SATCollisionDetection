package SAT;

import processing.core.*;

import java.util.ArrayList;

public class Main extends PApplet {

    private Polygon a;
    private Polygon b;

    @Override
    public void settings() {
        size(640, 480);
    }

    @Override
    public void setup() {
        a = new Polygon(new Vector2[]{
                new Vector2(200, 200),
                new Vector2(220, 100),
                new Vector2(250, 400)
        });
        b = new Polygon(new Vector2[]{
                new Vector2(mouseX, mouseY),
                new Vector2(mouseX + 40, mouseY + 40),
                new Vector2(mouseX, mouseY + 80),
                new Vector2(mouseX - 40, mouseY + 40)
        });
    }

    @Override
    public void draw() {
        noFill();
        background(204);
        b = new Polygon(new Vector2[]{
                new Vector2(mouseX, mouseY),
                new Vector2(mouseX + 40, mouseY + 40),
                new Vector2(mouseX, mouseY + 80),
                new Vector2(mouseX - 40, mouseY + 40)
        });
        if (Polygon.Collide(a, b)) {
            stroke(255, 0, 0);
        } else {
            stroke(0, 0, 0);
        }
        polygon(a);
        polygon(b);


    }

    private void polygon(Polygon a) {
        beginShape();
        for (var i = 0; i < a.vertices.length; i++) {
            vertex(a.vertices[i].x, a.vertices[i].y);
        }
        endShape(CLOSE);
    }

    public static void main(String[] args) {
        PApplet.main("SAT.Main");
    }

}

// 二次元ベクトル
class Vector2 {
    public float x; // x要素
    public float y; // y要素

    // コンストラクタ
    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    // 内積 Dot(a, b) = |a||b|cosθ
    public static float Dot(Vector2 a, Vector2 b) {
        return a.x * b.x + a.y * b.y;
    }

    // 法線ベクトル 垂直な直線のために必要
    public static Vector2 Normal(Vector2 a) {
        // 法線ベクトルは2個あるが今回の用途では片方だけでよい
        return new Vector2(a.y, a.x * -1);
    }

    // ベクトルの引き算
    public static Vector2 Minus(Vector2 a, Vector2 b) {
        return new Vector2(a.x - b.x, a.y - b.y);
    }
}

// 多角形
class Polygon {
    public Vector2[] vertices; // 頂点の座標の配列 時計回りの順になっている必要がある

    // コンストラクタ
    public Polygon(Vector2[] vertices) {
        this.vertices = vertices;
    }

    // 当たり判定本体
    public static boolean Collide(Polygon a, Polygon b) {
        // 軸のリスト
        var axes = getAxes(a, b);

        for (var i = 0; i < axes.size(); i++) {
            var ap = a.Projection(axes.get(i)); // 軸に対しての射影
            var bp = b.Projection(axes.get(i));

            if (!((ap[0] <= bp[0] && bp[0] <= ap[1]) || (bp[0] <= ap[0] && ap[0] <= bp[1]))) {
                return false; // 射影同士が重なっていないなら当たっていない
            }
        }
        return true; // すべての垂直なベクトルに対して射影が重なっていれば当たっている
    }

    // 多角形の軸に対する射影を得る
    private float[] Projection(Vector2 axis) {
        var min = Vector2.Dot(axis, vertices[0]);
        var max = min;

        for (var i = 1; i < vertices.length; i++) {
            var p = Vector2.Dot(axis, vertices[i]);
            if (p < min) {
                min = p;
            } else if (p > max) {
                max = p;
            }
        }
        return new float[]{min, max};
    }

    // それぞれの辺に対して垂直なベクトルのリストを作成する
    private static ArrayList<Vector2> getAxes(Polygon a, Polygon b) {
        var axes = new ArrayList<Vector2>();

        for (var i = 0; i < a.vertices.length; i++) {
            var p1 = a.vertices[i];
            var p2 = i == a.vertices.length - 1 ? a.vertices[0] : a.vertices[i + 1];
            axes.add(Vector2.Normal(Vector2.Minus(p2, p1)));
        }

        for (var i = 0; i < b.vertices.length; i++) {
            var p1 = b.vertices[i];
            var p2 = i == b.vertices.length - 1 ? b.vertices[0] : b.vertices[i + 1];
            axes.add(Vector2.Normal(Vector2.Minus(p2, p1)));
        }
        return axes;
    }
}

