# web-whiteboard-presentation

This is meant to be the presentation slide for the talk I want to give at the Boston Clojure Meetup

## Overview

This is just some slides to provide a facade of preparedness, and to give legible references to
where people can get more info if they like the talk.

## Setup

To get an interactive development environment run:

    lein figwheel

and open your browser at [localhost:3449](http://localhost:3449/).
This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL.

To clean all compiled files:

    lein clean

To create a production build run:

    lein do clean, cljsbuild once min

And open your browser in `resources/public/index.html`. You will not
get live reloading, nor a REPL.

## License

Copyright © 2017 Tom Kidd

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
