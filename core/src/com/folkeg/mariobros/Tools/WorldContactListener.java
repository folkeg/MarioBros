package com.folkeg.mariobros.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.Gdx;
import com.folkeg.mariobros.FireBalls.FireBall;
import com.folkeg.mariobros.Item.Item;
import com.folkeg.mariobros.MarioBros;
import com.folkeg.mariobros.Sprites.Enemy;
import com.folkeg.mariobros.Sprites.Turtle;
import com.folkeg.mariobros.TileObjects.Ground;
import com.folkeg.mariobros.TileObjects.InteractiveTilesObject;
import com.folkeg.mariobros.Sprites.Mario;

/**
 * Created by folkeg on 5/13/16.
 */
public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        int collisonDef = fixtureA.getFilterData().categoryBits |
                fixtureB.getFilterData().categoryBits;
        Gdx.app.log(String.valueOf(fixtureA.getFilterData().categoryBits),
                String.valueOf(fixtureB.getFilterData().categoryBits));

        switch (collisonDef){

            //if enemy hit the edge ground, turn around
            case MarioBros.ENEMY_BIT | MarioBros.GROUND_BIT:
                if(fixtureA.getFilterData().categoryBits == MarioBros.GROUND_BIT &&
                    ((InteractiveTilesObject)fixtureA.getUserData()).getObject().getProperties().containsKey("Edge")){
                    ((Enemy)fixtureB.getUserData()).reverseVelocity(true, false);
                }
                else if(fixtureB.getFilterData().categoryBits == MarioBros.GROUND_BIT &&
                        ((InteractiveTilesObject)fixtureB.getUserData()).getObject().getProperties().containsKey("Edge")){
                    ((Enemy)fixtureA.getUserData()).reverseVelocity(true, false);
                }
                break;
            case MarioBros.MARIO_HEAD_BIT | MarioBros.BRICK_BIT:
            case MarioBros.MARIO_HEAD_BIT | MarioBros.COIN_BIT:
                if(fixtureA.getFilterData().categoryBits == MarioBros.MARIO_HEAD_BIT){
                    ((InteractiveTilesObject)fixtureB.getUserData()).onHeadHit
                            ((Mario)fixtureA.getUserData());
                }
                else {
                    ((InteractiveTilesObject)fixtureA.getUserData()).onHeadHit
                            ((Mario)fixtureB.getUserData());
                }
                break;
            case MarioBros.ENEMY_HEAD_BIT | MarioBros.MARIO_BIT:
                if(fixtureA.getFilterData().categoryBits == MarioBros.ENEMY_HEAD_BIT){
                    ((Enemy)fixtureA.getUserData()).onHeadHit((Mario)fixtureB.getUserData());
                }
                else {
                    ((Enemy)fixtureB.getUserData()).onHeadHit((Mario)fixtureA.getUserData());
                }
                break;
            case MarioBros.ENEMY_BIT | MarioBros.OBJECT_BIT:
                if(fixtureA.getFilterData().categoryBits == MarioBros.ENEMY_BIT){
                    ((Enemy)fixtureA.getUserData()).reverseVelocity(true, false);
                }
                else {
                    ((Enemy)fixtureB.getUserData()).reverseVelocity(true, false);
                }
                Gdx.app.log("EnemyHitWall", "Reverse");
                break;
            case MarioBros.MARIO_BIT | MarioBros.ENEMY_BIT:
                if(fixtureA.getFilterData().categoryBits == MarioBros.MARIO_BIT){
                    ((Mario)fixtureA.getUserData()).onHit((Enemy)fixtureB.getUserData());
                }
                else {
                    ((Mario)fixtureB.getUserData()).onHit((Enemy)fixtureA.getUserData());
                }
                break;
            case MarioBros.ENEMY_BIT | MarioBros.ENEMY_BIT:
                Gdx.app.log("EnemtHitEnemy", "Reverse");
                ((Enemy)fixtureA.getUserData()).onEnemyHit((Enemy)fixtureB.getUserData());
                ((Enemy)fixtureB.getUserData()).onEnemyHit((Enemy)fixtureA.getUserData());;
                break;
            case MarioBros.ITEM_BIT | MarioBros.ITEM_BIT:
                Gdx.app.log("ITEMHITITEM", "Reverse");
                ((Item)fixtureA.getUserData()).reverseVelocity(true, false);
                ((Item)fixtureB.getUserData()).reverseVelocity(true, false);
                break;
            case MarioBros.ENEMY_BIT | MarioBros.ITEM_BIT:
                Gdx.app.log("EnemtHitITEM", "Reverse");
                if(fixtureA.getFilterData().categoryBits == MarioBros.ITEM_BIT){
                    ((Item)fixtureA.getUserData()).onEnemyHit((Enemy)fixtureB.getUserData());
                    ((Enemy)fixtureB.getUserData()).reverseVelocity(true,false);

                }
                else {
                    ((Item)fixtureB.getUserData()).onEnemyHit((Enemy)fixtureA.getUserData());
                    ((Enemy)fixtureA.getUserData()).reverseVelocity(true,false);
                }
                break;
            case MarioBros.ITEM_BIT | MarioBros.OBJECT_BIT:
                Gdx.app.log("MushRoomHitWall", "Reverse");
                if(fixtureA.getFilterData().categoryBits == MarioBros.ITEM_BIT){
                    ((Item)fixtureA.getUserData()).reverseVelocity(true, false);
                }
                else {
                    ((Item)fixtureB.getUserData()).reverseVelocity(true, false);
                }
                break;
            case MarioBros.ITEM_BIT | MarioBros.MARIO_BIT:
                Gdx.app.log("ItemHitMario", "Use");
                Gdx.app.log(fixtureB.getUserData().toString(), "fixtureB");
                Gdx.app.log(fixtureA.getUserData().toString(), "fixtureA");
                if(fixtureA.getFilterData().categoryBits == MarioBros.ITEM_BIT){
                    ((Item)fixtureA.getUserData()).use((Mario)fixtureB.getUserData());
                }
                else {
                    ((Item)fixtureB.getUserData()).use((Mario)fixtureA.getUserData());
                }
                break;
            case MarioBros.FIREBALL_BIT | MarioBros.ENEMY_BIT:
                if(fixtureA.getFilterData().categoryBits == MarioBros.FIREBALL_BIT){
                    ((FireBall)fixtureA.getUserData()).onEnemyHit((Enemy)fixtureB.getUserData());
                }
                else {
                    ((FireBall)fixtureB.getUserData()).onEnemyHit((Enemy)fixtureA.getUserData());
                }
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
