// copyright 2013 Lucas Samaruga samarugalucas@gmail.com

Circle {
	var <center;
	var <rad;
	var <diameter;

	*new { arg center = 0, rad = 0;
		^super.new.init(center, rad);
	}

	init { arg ce, ra;
		this.center = ce;
		this.rad = ra;
	}

	center_ { arg point;
		center = point.asPoint;
	}

	rad_ { arg val;
		rad = val;
		diameter = rad * 2.0;
	}

	diameter_ { arg val;
		diameter = val;
		rad = diameter / 2.0;
	}

	boundingRect {
		^Rect(center.x - rad, center.y - rad, diameter, diameter);
	}

	contains { arg point;
		if(rad.pow(2) >= ((point.x - center.x).pow(2) + (point.y - center.y).pow(2)), {
			^true;
		}, {
			^false
		});
	}
}

KeyCircle : Circle {
	var <>id;
}

KeyTrace {
	var <list;
	//var <>maxItems = 4;
	var >traceCompleteAction;

	*new {
		^super.new.init;
	}

	init {
		list = List.new;
	}

	// el trazo cierra cuando recibe nil o vuelve al primer valor
	add { arg id;
		if(id != list.last, { // solo almacena volores distintos al anterior
			if(id.isNil /*and: { list.notEmpty }*/, {
				//list.postln;
				traceCompleteAction.value(this);
				this.clear;
				^this;
			});
			if(list.first == id, {
				//list.postln;
				traceCompleteAction.value(this);
				this.clear;
			});
			list = list.add(id);
		});
	}

	clear {
		list = list.clear;
	}
}

KeyABC {
	var numPoints;
	var <dict;

	*new { arg numPoints;
		^super.new.init(numPoints);
	}

	init { arg np;
		numPoints = np;
		dict = [
			0.dup(numPoints),
			(1..numPoints),
			(2..numPoints)++1
		].flop;
		dict = dict ++ [
			0.dup(numPoints),
			[1]++(numPoints..2),
			(numPoints..1)
		].flop;
		dict = dict ++ [ // check
			0.dup(numPoints),
			(1..numPoints),
			(2..numPoints)++1,
			(3..numPoints)++[1, 2]
		].flop;
		dict = dict ++ [// chock
			0.dup(numPoints),
			[2, 1]++(numPoints..3),
			[1]++(numPoints..2),
			(numPoints..1)
		].flop;
	}

	atTrace { arg trace;
		dict.do({ arg i, j;
			if(i == trace.list.asArray, { ^(j+$a.ascii).asAscii });
		});
		//^nil;
		^$ ;
	}
}

KeyView {
	var view; // = UserView.new(bounds: Rect(0, 0, 400, 400));
	var initPos; // = Point(view.bounds.width/2, view.bounds.height/2);
	var numPoints; // = 8;
	var startAngle; // = 0; // 2pi * 0.083; // 0 es abajo, aumenta contra reloj
	// 1 / ( 2pi / (numPoinst*2) ) = 0.083... para 6 points
	var maxDist; // = 100;
	var prad; // = 20;
	var points;
	var trace;
	var abc;

	*new { arg bounds = Rect(0, 0, 400, 400), prad = 25;
		^super.new.init(bounds, prad);
	}

	init { arg bounds, pr;
		view = UserView.new(bounds: bounds);
		initPos = nil;
		numPoints = 8;
		startAngle = 0;
		maxDist = 100;
		prad = pr;
		points = [];
		trace = KeyTrace.new;
		abc = KeyABC.new(numPoints);
		trace.traceCompleteAction = { arg trace;
			abc.atTrace(trace).post;
		};
		this.initView;
		view.front;
	}

	initView {
		view.drawFunc = { arg view;
			points.do({ arg p;
				Pen.strokeOval(Rect.aboutPoint(p.center, p.rad, p.rad));
			});
		};
		view.mouseDownAction = { arg view, x, y;
			var auxPoint;
			initPos = KeyCircle(Point(x, y), prad).id_(0);
			this.calcPoints;
			view.refresh;
			auxPoint = this.pointFromPos(x, y);
			// if no necesario, siempre es el centro del centro salvo error
			if(auxPoint.notNil, { trace.add(auxPoint.id) });
		};
		view.mouseMoveAction = { arg view, x, y;
			var auxPoint = this.pointFromPos(x, y);
			if(auxPoint.notNil, { trace.add(auxPoint.id) });
		};
		view.mouseUpAction = { arg view, x, y;
			initPos = nil;
			this.calcPoints;
			trace.add(nil); // add nil para limpiar y llamar a la acci'on
			view.refresh;
		};
	}

	calcPoints {
		var step = startAngle;
		var auxPoint;
		points = [];
		initPos !? {
			points = points.add(initPos);
			numPoints.do({ arg i;
				auxPoint = Point(sin(step) * maxDist, cos(step) * maxDist) + initPos.center;
				points = points.add(KeyCircle(auxPoint, prad).id_(i+1));
				step = (2pi / numPoints) + step;
			});
		};
	}

	pointFromPos { arg x, y;
		points.do({ arg p;
			if(p.contains(Point(x, y)), {
				^p;
			});
		});
		^nil;
	}
}

/*
KeyView.new(prad: 38);
*/
