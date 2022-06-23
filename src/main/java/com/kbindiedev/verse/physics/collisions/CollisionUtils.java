package com.kbindiedev.verse.physics.collisions;

import com.kbindiedev.verse.math.shape.Polygon;
import com.kbindiedev.verse.math.shape.Rectanglef;
import org.joml.Vector3f;

/** A collection of collision checking methods. */
public class CollisionUtils {

    /** @return whether or not the two rectangles that are assumed to be AABB (axis aligned bounding boxes) are overlapping. */
    public static boolean collidesAABB(Rectanglef r1, Rectanglef r2) {
        return (r1.getX() < r2.getX2() && r1.getX2() > r2.getX()) &&
               (r1.getY() < r2.getY2() && r1.getY2() > r2.getY());
    }

    // TODO: float vs int etc
    // TODO: GJK (?), this is O(n*m), n = num points in p1, m = num points in p2
    // heavily inspired by DIAG - https://www.youtube.com/watch?v=7Ik2vowGcU0&ab_channel=javidx9

    public static Vector3f testPolygonCollision(Polygon p1, Polygon p2) {

        return new Vector3f();
    }
        /*
        Polygon poly1 = p1, poly2 = p2;
        Point2Df center = poly1.getCenter();
        Vector2f poly1OldCenter = new Vector2f(center.getX(), center.getY());

        Vector2f displacement = new Vector2f();


        for (int i = 0; i < 2; ++i) {

            if (i == 1) {
                poly1 = p2;
                poly2 = p1;
            }

            Iterator<Point2Df> points = poly1.iteratePoints();
            while (points.hasNext()) {

                Line<Point2Df> diagonal = new Line<>(poly1.getCenter(), points.next());
                displacement.set(0f, 0f);

                Iterator<Line<Point2Df>> edges = poly2.iterateLines();
                while (edges.hasNext()) {
                    Line<Point2Df> edge = edges.next();

                    // simple line segment intersection algorithm
                    float r1sx = diagonal.getP1().getX(), r1ex = diagonal.getP2().getX();
                    float r1sy = diagonal.getP1().getY(), r1ey = diagonal.getP2().getY();
                    float r2sx = edge.getP1().getX(), r2ex = edge.getP2().getX();
                    float r2sy = edge.getP1().getY(), r2ey = edge.getP2().getY();

                    float h = (r2ex - r2sx) * (r1sy - r1ey) - (r1sx - r1ex) * (r2ey - r2sy);
                    float t1 = (r2sy - r2ey) * (r1sx - r2sx) + (r2ex - r2sx) * (r1sy - r2sy);
                    float t2 = (r1sy - r1ey) * (r1sx - r2sx) + (r1ex - r1sx) * (r1sy - r2sy);
                    t1 /= h;
                    t2 /= h;

                    if (t1 >= 0f && t1 <= 1f && t2 >= 0f && t2 <= 1f) {
                        displacement.x += (1f - t1) * (r1ex - r1sx);
                        displacement.y += (1f - t1) * (r1ey - r1sy);
                    }

                }

                if (i == 0) displacement.mul(-1);
                p1.translate(displacement);

            }


        }

        Vector2f totalDisplacement = new Vector2f(p1.getCenter().getX() - poly1OldCenter.x, p1.getCenter().getY() - poly1OldCenter.y);
        p1.translateTo(poly1OldCenter);

        return totalDisplacement;

    }
    */


}
