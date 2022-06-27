package com.kbindiedev.verse.physics.collisions;

import com.kbindiedev.verse.math.helpers.Line;
import com.kbindiedev.verse.math.shape.Polygon;
import com.kbindiedev.verse.math.shape.Rectanglef;
import com.kbindiedev.verse.physics.CollisionManifold;
import com.kbindiedev.verse.physics.PhysicsRigidBody;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/** A collection of collision checking methods. */
public class CollisionUtil {

    public static CollisionManifold checkCollisions(PhysicsRigidBody body1, PhysicsRigidBody body2) {
        // TODO: List of manifolds? combine fixtures to single shape ??
        //return testPolygonCollision(body1.getFixtures().get(0).getShape(), body2.getFixtures().get(0).getShape());
        return testAABBCollision(body1.getFixtures().get(0).getShape(), body2.getFixtures().get(0).getShape());
    }

    // TODO: input AABB, for now assume polygons are AABB
    public static CollisionManifold testAABBCollision(Polygon p1, Polygon p2) {
        List<Vector3f> points = p1.getPoints();
        Rectanglef r1 = Rectanglef.newByCoordinates(points.get(0).x, points.get(0).y, points.get(2).x, points.get(2).y);
        points = p2.getPoints();
        Rectanglef r2 = Rectanglef.newByCoordinates(points.get(0).x, points.get(0).y, points.get(2).x, points.get(2).y);

        // from r1 into r2
        float topPenetrationDepth = r2.getY2() - r1.getY();
        float bottomPenetrationDepth = r1.getY2() - r2.getY();
        float rightPenetrationDepth = r2.getX2() - r1.getX();
        float leftPenetrationDepth = r1.getX2() - r2.getX();

        int smallest = 0; // 0=top, 1=bottom, 2=right, 3=left
        float currentDepth = topPenetrationDepth;
        if (bottomPenetrationDepth < currentDepth) { smallest = 1; currentDepth = bottomPenetrationDepth; }
        if (rightPenetrationDepth < currentDepth) { smallest = 2; currentDepth = rightPenetrationDepth; }
        if (leftPenetrationDepth < currentDepth) { smallest = 3; currentDepth = leftPenetrationDepth; }

        //if (currentDepth < 0f) return new CollisionManifold(new ArrayList<>(), new Vector3f(), 0f);
        if (currentDepth < 0f) return null;

        List<Vector3f> contacts = new ArrayList<>();
        Vector3f normal = new Vector3f();

        float xOff = leftPenetrationDepth - r1.getWidth() / 2;
        if (xOff < 0f) xOff = 0f;
        if (xOff > r2.getWidth()) xOff = r2.getWidth();
        float yOff = bottomPenetrationDepth - r1.getHeight() / 2;
        if (yOff < 0f) yOff = 0f;
        if (yOff > r2.getHeight()) yOff = r2.getHeight();

        switch (smallest) {
            case 0: // top
                contacts.add(new Vector3f(r2.getX() + xOff, r2.getY2(), 0f));
                normal.set(0f, 1f, 0f);
                break;
            case 1: //bottom
                contacts.add(new Vector3f(r2.getX() + xOff, r2.getY(), 0f));
                normal.set(0f, -1f, 0f);
                break;
            case 2: //right
                contacts.add(new Vector3f(r2.getX2(), r2.getY() + yOff, 0f));
                normal.set(1f, 0f, 0f);
                break;
            case 3: //left
                contacts.add(new Vector3f(r2.getX(), r2.getY() + yOff, 0f));
                normal.set(-1f, 0f, 0f);
                break;
        }

        return new CollisionManifold(contacts, normal, currentDepth);

    }





    /** @return whether or not the two rectangles that are assumed to be AABB (axis aligned bounding boxes) are overlapping. */
    public static boolean collidesAABB(Rectanglef r1, Rectanglef r2) {
        return (r1.getX() < r2.getX2() && r1.getX2() > r2.getX()) &&
               (r1.getY() < r2.getY2() && r1.getY2() > r2.getY());
    }

    // TODO: float vs int etc
    // TODO: GJK (?), this is O(n*m), n = num points in p1, m = num points in p2
    // heavily inspired by DIAG - https://www.youtube.com/watch?v=7Ik2vowGcU0&ab_channel=javidx9
    // TODO: normal and depth
    // TODO 2: buggy
    public static CollisionManifold testPolygonCollision(Polygon p1, Polygon p2) {

        Polygon poly1 = p1, poly2 = p2;
        Vector3f center = poly1.getCenter();

        List<Vector3f> contactPoints = new ArrayList<>();
        Vector3f normal = new Vector3f();
        float depth = 0f;

        for (int i = 0; i < 2; ++i) {

            if (i == 1) {
                poly1 = p2;
                poly2 = p1;
            }

            for (Vector3f point1 : poly1.getPoints()) {

                Line<Vector3f> diagonal = new Line<>(poly1.getCenter(), point1);

                List<Vector3f> p2points = poly2.getPoints();
                Vector3f prevPoint = p2points.get(p2points.size() - 1);
                for (Vector3f point2 : p2points) {
                    Line<Vector3f> edge = new Line<>(prevPoint, point2);

                    // simple line segment intersection algorithm
                    float r1sx = diagonal.getFirst().x(), r1ex = diagonal.getSecond().x();
                    float r1sy = diagonal.getFirst().y(), r1ey = diagonal.getSecond().y();
                    float r2sx = edge.getFirst().x(), r2ex = edge.getSecond().x();
                    float r2sy = edge.getFirst().y(), r2ey = edge.getSecond().y();

                    float h = (r2ex - r2sx) * (r1sy - r1ey) - (r1sx - r1ex) * (r2ey - r2sy);
                    float t1 = (r2sy - r2ey) * (r1sx - r2sx) + (r2ex - r2sx) * (r1sy - r2sy);
                    float t2 = (r1sy - r1ey) * (r1sx - r2sx) + (r1ex - r1sx) * (r1sy - r2sy);
                    t1 /= h;
                    t2 /= h;

                    if (t1 >= 0f && t1 <= 1f && t2 >= 0f && t2 <= 1f) {
                        Vector3f diagDir = new Vector3f(diagonal.getSecond()).sub(diagonal.getFirst());
                        Vector3f edgeDir = new Vector3f(edge.getSecond()).sub(edge.getFirst());

                        contactPoints.add(diagDir.mul(t1).add(diagonal.getFirst()));
                        contactPoints.add(edgeDir.mul(t2).add(edge.getFirst()));
                    }

                }

            }

        }

        return new CollisionManifold(contactPoints, normal, depth);

    }


}