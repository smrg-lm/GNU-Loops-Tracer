c = Circle.new(Point(50, 50), 50);
c.center;
c.rad;
c.diameter;
c.boundingRect;
c.center = Point(543, 123);
c.boundingRect;
c.center = 10;
c.diameter = 25;

(
	var view = UserView.new(bounds: Rect(0, 0, 400, 400));
	var initPos = Point(view.bounds.width/2, view.bounds.height/2);
	var numPoints = 8;
	var startAngle = 0; // 2pi * 0.083; // 0 es abajo, aumenta contra reloj
	// 1 / ( 2pi / (numPoinst*2) ) = 0.083... para 6 points
	var maxDist = 100;
	var prad = 20;
	var points;

	var calcPoints = {
		var step = startAngle;
		var auxPoint;

		points = [];

		initPos !? {
			points = points.add(initPos);
			numPoints.do({ arg i;
				auxPoint = Point(sin(step) * maxDist, cos(step) * maxDist) + initPos;
				points = points.add(auxPoint);
				step = (2pi / numPoints) + step;
			});
		};
	};

	var pointFromPos = { arg x, y;
		var ret;

		points.do({ arg p;
			if(prad.pow(2) >= ((x - p.x).pow(2) + (y - p.y).pow(2)), {
				ret = p;
			});
		});

		ret;
	};

	view.drawFunc = { arg view;
		points.do({ arg p;
			Pen.strokeOval(Rect.aboutPoint(p, prad, prad));
		});
	};

	view.mouseDownAction = { arg view, x, y;
		initPos = Point(x, y);
		calcPoints.value();
		view.refresh;
		pointFromPos.value(x, y).postln;
	};

	view.mouseMoveAction = { arg view, x, y;
		pointFromPos.value(x, y).postln;
	};

	view.mouseUpAction = { arg view, x, y;
		initPos = nil;
		calcPoints.value();
		view.refresh;
	};

	view.front;
)
