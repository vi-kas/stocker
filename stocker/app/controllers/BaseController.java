package controllers;

import akka.actor.ActorSystem;
import play.mvc.Controller;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by vika on 16/08/16.
 */
@Singleton
public class BaseController extends Controller {

    @Inject
    ActorSystem system;

}
